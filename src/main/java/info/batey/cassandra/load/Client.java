package info.batey.cassandra.load;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import info.batey.cassandra.load.distributions.OperationStream;
import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client {
    private Session session;
    private List<Request> outstandingRequests = new ArrayList<>();
    private Histogram results = new Histogram(3);
    private Cluster cluster;
    private long success;
    private long fail;
    private final OperationStream ops;

    public Client(OperationStream ops) {
        this.ops = ops;
        this.cluster = Cluster.builder()
                .addContactPoint("localhost")
                .build();
        this.session = cluster.connect("test");
    }

    long execute() {
        // check existing calls
        outstanding();
        // add another call
        // todo check max number of outstanding requests
        Request next = ops.next().execute(session);
        outstandingRequests.add(next);
        return 1;
    }

    Result finish() {
        while (!outstandingRequests.isEmpty()) {
            outstanding();
        }
        cluster.closeAsync();
        return new Result(results, success, fail);
    }

    private void outstanding() {
        Iterator<Request> iter = outstandingRequests.iterator();
        while (iter.hasNext()) {
            Request next = iter.next();
            if (next.future.isDone()) {
                // if this thread isn't scheduled often enough or of the executeAsync call blocks
                // then response time will be over reported
                iter.remove();
                results.recordValue(System.nanoTime() - next.startTime);
                try {
                    ResultSet rs = next.future.getUninterruptibly();
                    // do something meaningful
                    success++;
                } catch (Exception e) {
                    e.printStackTrace();
                    fail++;
                }
            }
        }
    }

    static class Result {
        Histogram histogram;
        long success;
        long fail;

        public Result(Histogram histogram, long success, long fail) {
            this.histogram = histogram;
            this.success = success;
            this.fail = fail;
        }
    }

}
