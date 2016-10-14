package info.batey.cassandra.load;

import info.batey.cassandra.load.config.CloadCli;
import org.junit.Test;

public class CloadTest {
    CloadCli.ProfileCommand config = new CloadCli.ProfileCommand();
    @Test
    public void createsSchema() throws Exception {
        Cload cload = new Cload(null, null, config, null);
        // todo test schema with mockito
    }

    @Test
    public void shutdownShouldNotThrow() throws Exception {
        new Cload(null, null, config, null).shutdown();
    }
}