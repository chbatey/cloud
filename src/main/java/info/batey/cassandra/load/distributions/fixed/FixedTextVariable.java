package info.batey.cassandra.load.distributions.fixed;

import info.batey.cassandra.load.distributions.VariableGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class FixedTextVariable implements VariableGenerator<String> {

    private static Logger LOG = LogManager.getLogger(FixedTextVariable.class);

    private final int number;
    private final long seed = System.currentTimeMillis();
    private Random generator = new Random(seed);

    private long used = 0;

    public FixedTextVariable(int number) {
        LOG.debug("Using seed {}", seed);
        this.number = number;
    }

    @Override
    public String next() {
        if (used == number) {
            used = 0;
            generator = new Random(seed);
        }
        used++;
        return String.valueOf(generator.nextLong());
    }
}
