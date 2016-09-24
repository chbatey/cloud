package info.batey.cassandra.load.concurrency;

import info.batey.cassandra.load.config.CloadCli;
import org.jctools.queues.MpscArrayQueue;
import org.junit.Test;

public class CoreTest {
    @Test
    public void something() throws Exception {
        CloadCli.ProfileCommand profile = new CloadCli.ProfileCommand();
        new Core(profile, new MpscArrayQueue<>(1));
        // todo test something
    }
}
