package info.batey.cassandra.load.drivers;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import info.batey.cassandra.load.Request;
import info.batey.cassandra.load.distributions.OperationStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Driver {

    private final Cluster cluster;
    private Result result;
    private final Session session;
    private List<Request> outstandingRequests = new ArrayList<>();
    private final OperationStream ops;

    public Driver(OperationStream ops, Cluster cluster, Result result) {
        this.ops = ops;
        this.cluster = cluster;
        this.result = result;
        this.session = cluster.connect();
    }

   public void replaceResult(Result result) {
       this.result = result;
   }

    public long execute() {
        // check existing calls
        outstanding();
        // add another call
        // todo check max number of outstanding requests
        Request next = ops.next().execute(session);
        outstandingRequests.add(next);
        return 1;
    }

    public void finish() {
        while (!outstandingRequests.isEmpty()) {
            outstanding();
        }
        cluster.closeAsync();
    }

    private void outstanding() {
        Iterator<Request> iter = outstandingRequests.iterator();
        while (iter.hasNext()) {
            Request next = iter.next();
            if (next.getFuture().isDone()) {
                // if this thread isn't scheduled often enough or of the executeAsync call blocks
                // then response time will be over reported
                iter.remove();
                result.getHistogram().recordValue(System.nanoTime() - next.getStartTime());
                try {
                    ResultSet rs = next.getFuture().getUninterruptibly();
                    // do something meaningful
                    result.success();
                } catch (Exception e) {
                    e.printStackTrace();
                    result.fail();
                }
            }
        }
    }

}
