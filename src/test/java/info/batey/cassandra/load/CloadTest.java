package info.batey.cassandra.load;

import org.junit.Test;

public class CloadTest {
    @Test
    public void createsSchema() throws Exception {
        Cload cload = new Cload(null, null, null, streamFactory);

        // todo test schema with mockito
    }

    @Test
    public void shutdownShouldNotThrow() throws Exception {
        new Cload(null, null, null, streamFactory).shutdown();
    }
}