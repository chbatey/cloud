package info.batey.cassandra.load;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.HdrHistogram.Histogram;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class Cload {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        SingleCommand<Stress> stress = SingleCommand.singleCommand(Stress.class);
        Stress config = stress.parse(args);

        int nrCores = config.cores == 0 ? Runtime.getRuntime().availableProcessors() : config.cores;
        System.out.println("Cores: " + nrCores);
        long nrRequests = config.requests == 0 ? 1000 : config.requests;
        long totalRequests = nrCores * nrRequests;
        ExecutorService es = Executors.newFixedThreadPool(nrCores);
        List<Core> cores = range(0, nrCores).mapToObj(i -> new Core(nrRequests, config.connections)).collect(toList());
        cores.forEach(Core::init);


        long start = System.nanoTime();
        List<Future<Core.CoreResults>> results = cores.stream().map(es::submit).collect(toList());


        int totalSuccess = 0;
        int totalFail = 0;
        Histogram total = new Histogram(3);
        for (Future<Core.CoreResults> f : results) {
            Core.CoreResults result = f.get();
            totalSuccess += result.success;
            totalFail += result.failure;
        }

        // ok ok not that accurate
        long end = System.nanoTime();
        long totalTime = end - start;

        total.outputPercentileDistribution(System.out, 1000.0);

        System.out.println("Success: " + totalSuccess);
        System.out.println("Failures: " + totalFail);
        System.out.println("Total duration: " + Duration.ofNanos(totalTime));
        System.out.println("Assuming even load. TPS: " + totalRequests / (totalTime / 1000000000f));

        es.shutdownNow();

        System.out.println("Finished, press enter to shut down JVM...");
        System.in.read();
    }

    @Command(name = "stress", description = "kills your cassandra cluster")
    public static class Stress {
        @Option(name = "-c", description = "Number of cores to use")
        public int cores;

        @Option(name = "-r", description = "Number of requests per core")
        public int requests;

        @Option(name = "-t", description = "Number of connections per core")
        public int connections = 10;
    }


}
