package info.batey.cassandra.load;

import com.datastax.driver.core.ResultSetFuture;


/**
 * Designed to be used from a single thread.
 */
public class HardcodedSillyReads implements OperationStream {

    @Override
    public Operation next() {
        return session -> {
            ResultSetFuture result = session.executeAsync("select * from kv where key = ?", "chbatey");
            return new Request(result);
        };
    }
}
