package info.batey.cassandra.load.config;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

@Command(name = "stress", description = "kills your cassandra cluster")
public class CLI {
    @Option(name = "-c", description = "Number of cores to use")
    public int cores = Runtime.getRuntime().availableProcessors();

    @Option(name = "-r", description = "Number of requests per core")
    public int requests;

    @Option(name = "-t", description = "Number of connections per core")
    public int connections = 10;

    @Option(name = "-p", description = "Profile for the run")
    public String profile;
}
