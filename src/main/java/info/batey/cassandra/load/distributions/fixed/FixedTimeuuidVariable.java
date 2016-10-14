package info.batey.cassandra.load.distributions.fixed;

import com.datastax.driver.core.utils.UUIDs;
import info.batey.cassandra.load.distributions.VariableGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;

public class FixedTimeuuidVariable implements VariableGenerator<UUID> {

    private static Logger LOG = LogManager.getLogger(FixedTextVariable.class);

    private final int number;
    private final long seed = System.currentTimeMillis();
    private Random generator = new Random(seed);

    private long used = 0;

    public FixedTimeuuidVariable(int number) {
        LOG.debug("Using seed {}", seed);
        this.number = number;
    }

    @Override
    public UUID next() {
        if (used == number) {
            used = 0;
            generator = new Random(seed);
        }
        used++;

        return UUIDs.endOf(generator.nextLong());
    }
}
