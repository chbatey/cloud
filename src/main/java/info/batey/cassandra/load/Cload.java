package info.batey.cassandra.load;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.parser.errors.ParseOptionMissingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import info.batey.cassandra.load.concurrency.Core;
import info.batey.cassandra.load.config.CloadCli;
import info.batey.cassandra.load.config.Profile;
import info.batey.cassandra.load.distributions.*;
import info.batey.cassandra.load.drivers.Result;
import info.batey.cassandra.load.schema.SchemaCreator;
import org.HdrHistogram.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jctools.queues.MpscArrayQueue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class Cload {

    private static final Logger LOG = LogManager.getLogger(Cload.class);
    private final SchemaCreator schemaCreator;
    private final Profile profile;
    private final CloadCli.ProfileCommand config;
    private final OperationStreamFactory streamFactory;

    /*
        Queue for the single threaded cores to report results. Single reporting thread will read the results
        and add them to a central histogram.
     */
    private final MpscArrayQueue<Result> reportStream;

    private ExecutorService coreExecutor;

    private List<Core> cores;

    private long totalRequests;
    private long start;

    public Cload(SchemaCreator schemaCreator, Profile profile, CloadCli.ProfileCommand stress, OperationStreamFactory streamFactory) {
        this.schemaCreator = schemaCreator;
        this.profile = profile;
        this.config = stress;
        this.streamFactory = streamFactory;
        this.reportStream = new MpscArrayQueue<>(config.cores * 2);
    }


    public void init() {
        schemaCreator.createSchema(profile);
        totalRequests = config.cores * config.requests;
        coreExecutor = Executors.newFixedThreadPool(config.cores);
        cores = range(0, config.cores).mapToObj(i -> new Core(i, config, reportStream)).collect(toList());

        cores.forEach(c -> {
            OperationStream ops = streamFactory.createStream(profile.getStatements(), config);
            c.init(ops);
        });
    }

    public void start() {
        LOG.info("Starting cores");
        start = System.nanoTime();
        cores.forEach(core -> {
            coreExecutor.submit(core);
        });
    }

    public void watchResults() throws ExecutionException, InterruptedException {
        // Build results in different (tested) class that can be serialized
        int totalSuccess = 0;
        int totalFail = 0;
        int finished = 0;
        long reports = 0;
        Histogram total = new Histogram(3);

        while (finished != config.cores) {
            Result result = reportStream.poll();
            if (result == null) {
                continue;
            }
            LOG.debug("Received result {}", result);
            reports++;
            if (result.isFinished()) finished++;
            totalFail += result.getFail();
            totalSuccess += result.getSuccess();
            total.add(result.getHistogram());
            if (reports % config.cores == 0) {
               // print the results out
                double percentileAtOrBelowValue = total.getValueAtPercentile(99) / 1000; // convert to microseconds
                System.out.printf("\rSuccess: %d Fail: %d 99-ile:  %f", totalSuccess, totalFail, percentileAtOrBelowValue);
            }
        }
        System.out.println();

        coreExecutor.shutdown();

        // ok ok not that accurate
        long end = System.nanoTime();
        long totalTime = end - start;

        total.outputPercentileDistribution(System.out, 1000.0);

        System.out.println("Success: " + totalSuccess);
        System.out.println("Failures: " + totalFail);
        System.out.println("99%ile: " + total.getPercentileAtOrBelowValue(99) / 1000);
        Duration duration = Duration.ofNanos(totalTime);
        System.out.printf("Duration: %d seconds\n", duration.getSeconds());
        System.out.println("Assuming even load. TPS: " + totalRequests / (totalTime / 1000000000f));

    }

    public void shutdown() {
        if (coreExecutor != null) {
            coreExecutor.shutdown();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Cluster cluster = null;
        Cload cload = null;
        try {
            Cli<Runnable> cli = new Cli<>(CloadCli.class);
            Runnable mode = cli.parse(args);
            mode.run();

            if (mode instanceof Help) {
                System.exit(0);
            } else if (mode instanceof CloadCli.ProfileCommand) {

                CloadCli.ProfileCommand config = (CloadCli.ProfileCommand) mode;

                cluster = Cluster.builder()
                        .withClusterName("schema creator")
                        .addContactPoint("localhost")
                        .build();

                Profile profile = Profile.parse(config.profile);

                OperationStreamFactory opsFactory = new OperationStreamFactory();

                cload = new Cload(new SchemaCreator(cluster.connect()), profile, config, opsFactory);
                cload.init();
                cluster.closeAsync();
                cload.start();
                cload.watchResults();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist: " + e.getMessage());
        } catch (ParseOptionMissingException e) {
            System.out.println("Mandatory option missing: " + e.getOptionTitle());
        } catch (NoHostAvailableException e) {
            LOG.debug("Unable to connect for schema creation", e);
            System.out.println("Unable to connect to schema: " + e.getMessage());
        }
        finally {
            if (cluster != null) cluster.close();
            if (cload != null) cload.shutdown();
        }

    }
}
