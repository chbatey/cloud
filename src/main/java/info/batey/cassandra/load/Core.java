package info.batey.cassandra.load;

import org.HdrHistogram.Histogram;

import java.util.concurrent.Callable;
import java.util.stream.IntStream;

public class Core implements Callable<Core.CoreResults> {

    private final long totalRequsts;
    private final int nrClients;
    private Client[] clients;
    private int currentTotal = 0;
    private long startTime;

    public Core(long requests, int connections) {
        totalRequsts = requests;
        nrClients = connections;
    }

    public void init() {
        OperationStream reads = new HardcodedSillyReads();
        OperationStream writes = new SimpleStatementWrite("insert INTO kv (key , value ) values (?, ?)",
                new TextColumnGenerator("three"),
                new TextColumnGenerator("four"));

        OperationStream ops = new CombinedOperationStream(reads, writes);
        clients = IntStream.range(0, nrClients).mapToObj(i -> new Client(ops)).toArray(Client[]::new);
    }

    private void run() {
        startTime = System.nanoTime();
        while (currentTotal < totalRequsts) {
            for (Client client : clients) {
                currentTotal += client.execute();
            }
        }
    }

    private CoreResults finish() {
        Histogram total = new Histogram(3);
        long success = 0;
        long fail = 0;
        for (Client client : clients) {
            Client.Result result = client.finish();
            total.add(result.histogram);
            success += result.success;
            fail += result.fail;
        }
        long totalTime = System.nanoTime() - startTime;

        return new CoreResults(total, success, fail, totalTime);
    }

    @Override
    public CoreResults call() throws Exception {
            run();
            return finish();
    }

    public static class CoreResults {
        final Histogram histogram;
        final long success;
        final long failure;
        final long totalTime;

        public CoreResults(Histogram histogram, long success, long failure, long totalTime) {
            this.histogram = histogram;
            this.success = success;
            this.failure = failure;
            this.totalTime = totalTime;
        }
    }
}
