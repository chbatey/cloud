package info.batey.cassandra.load;

import com.datastax.driver.core.ResultSetFuture;

class Request {
    final ResultSetFuture future;
    final long startTime;

    public Request(ResultSetFuture future) {
        this.future = future;
        this.startTime = System.nanoTime();
    }
}
