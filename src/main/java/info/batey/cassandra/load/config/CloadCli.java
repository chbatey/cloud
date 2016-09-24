package info.batey.cassandra.load.config;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.Help;

@Cli(name = "cload",
        defaultCommand = CloadCli.ProfileCommand.class,
        commands = { CloadCli.ProfileCommand.class, Help.class}
)
public class CloadCli {

    @Command(name = "stress", description = "kills your cassandra cluster")
    public static class ProfileCommand implements Runnable {
        @Option(name = "-c", description = "Number of cores to use")
        public int cores = Runtime.getRuntime().availableProcessors();

        @Option(name = "-r", description = "Number of requests per core")
        public int requests = 1000;

        @Option(name = "-t", description = "Number of drivers per core")
        public int drivers = 1;

        @Required
        @Option(name = "-p", title = "profile", description = "Profile for the run")
        public String profile;

        @Option(name = "share-driver", description = "TODO: Whether the cores share the same instance of the database driver")
        public boolean shareDriver = false;

        @Option(name = "-report-interval", description = "How often to report stats")
        public int reportFrequencySeconds = 5;

        @Override
        public void run() {
        }
    }
}

