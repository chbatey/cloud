package info.batey.cassandra.load;

import com.datastax.driver.core.ResultSetFuture;

public class Request {
    final ResultSetFuture future;
    final long startTime;

    public Request(ResultSetFuture future) {
        this.future = future;
        this.startTime = System.nanoTime();
    }

    public ResultSetFuture getFuture() {
        return future;
    }

    public long getStartTime() {
        return startTime;
    }
}
