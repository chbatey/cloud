package info.batey.cassandra.load;

import com.datastax.driver.core.Cluster;
import com.github.rvesse.airline.SingleCommand;
import info.batey.cassandra.load.config.CLI;
import info.batey.cassandra.load.config.Profile;
import info.batey.cassandra.load.distributions.*;
import info.batey.cassandra.load.schema.SchemaCreator;
import org.HdrHistogram.Histogram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class Cload {

    private final SchemaCreator schemaCreator;
    private final Profile profile;
    private final CLI stressConfig;
    private final OperationStreamFactory streamFactory;

    private ExecutorService es;

    private List<Core> cores;
    private List<Future<Core.CoreResults>> results;

    private long totalRequests;
    private long start;

    public Cload(SchemaCreator schemaCreator, Profile profile, CLI stress, OperationStreamFactory streamFactory) {
        this.schemaCreator = schemaCreator;
        this.profile = profile;
        this.stressConfig = stress;
        this.streamFactory = streamFactory;
    }


    public void init() {
        schemaCreator.createSchema(profile);

        long nrRequests = stressConfig.requests == 0 ? 1000 : stressConfig.requests;
        totalRequests = stressConfig.cores * nrRequests;
        es = Executors.newFixedThreadPool(stressConfig.cores);
        cores = range(0, stressConfig.cores).mapToObj(i -> new Core(nrRequests, stressConfig.connections)).collect(toList());


        cores.forEach(c -> {
            OperationStream ops = streamFactory.createStream(profile.getStatements(), stressConfig);
            c.init(ops);
        });
    }

    public void start() {
        start = System.nanoTime();
        results = cores.stream().map(es::submit).collect(toList());
    }

    public void awaitFinish() throws ExecutionException, InterruptedException {
        // Build results in different (tested) class that can be serialized
        int totalSuccess = 0;
        int totalFail = 0;
        Histogram total = new Histogram(3);
        for (Future<Core.CoreResults> f : results) {
            Core.CoreResults result = f.get();
            totalSuccess += result.success;
            totalFail += result.failure;
            total.add(result.histogram);
        }

        // ok ok not that accurate
        long end = System.nanoTime();
        long totalTime = end - start;

        total.outputPercentileDistribution(System.out, 1000.0);

        System.out.println("Success: " + totalSuccess);
        System.out.println("Failures: " + totalFail);
        System.out.println("Total duration: " + Duration.ofNanos(totalTime));
        System.out.println("Assuming even load. TPS: " + totalRequests / (totalTime / 1000000000f));

    }

    public void shutdown() {
        if (es != null) {
            es.shutdown();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Cluster cluster = null;
        Cload cload = null;
        try {
            SingleCommand<CLI> toParse = SingleCommand.singleCommand(CLI.class);
            CLI stressConfig = toParse.parse(args);
            Profile profile = Profile.parse(stressConfig.profile);

            cluster = Cluster.builder()
                    .addContactPoint("localhost")
                    .build();

            OperationStreamFactory opsFactory = new OperationStreamFactory();

            cload = new Cload(new SchemaCreator(cluster.connect()), profile, stressConfig, opsFactory);
            cload.init();
            cload.start();
            cload.awaitFinish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) cluster.close();
            if (cload != null) cload.shutdown();
        }

    }
}
