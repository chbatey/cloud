package info.batey.cassandra.load.concurrency;

import com.datastax.driver.core.Cluster;
import info.batey.cassandra.load.drivers.Driver;
import info.batey.cassandra.load.config.CloadCli;
import info.batey.cassandra.load.distributions.*;
import info.batey.cassandra.load.drivers.Result;
import org.HdrHistogram.Histogram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jctools.queues.MpscArrayQueue;

import java.util.stream.IntStream;

public class Core implements Runnable {

    private final static Logger LOG = LogManager.getLogger(Core.class);

    private final long totalRequests;
    private final int nrDrivers;
    private final long reportInterval;
    private final int core;
    private final MpscArrayQueue<Result> reportStream;
    private Driver[] clients;
    private int currentTotal = 0;
    private long lastReport;
    private Result currentResult;

    public Core(int core, CloadCli.ProfileCommand stressConfig, MpscArrayQueue<Result> reportStream) {
        this.core = core;
        this.reportStream = reportStream;
        this.totalRequests = stressConfig.requests;
        this.nrDrivers = stressConfig.drivers;
        this.reportInterval = stressConfig.reportFrequencySeconds * 1000;
    }

    public void init(OperationStream ops) {
        currentResult = new Result(new Histogram(3));
        clients = IntStream.range(0, nrDrivers).mapToObj(i -> {
            Cluster cluster = Cluster.builder()
                    .withClusterName("Core " + core)
                    .addContactPoint("localhost")
                    .build();
            return new Driver(ops, cluster, currentResult);
        }).toArray(Driver[]::new);
    }

    @Override
    public void run() {
        LOG.debug("Core {} starting", core);
        lastReport = System.currentTimeMillis();
        while (currentTotal < totalRequests) {
            LOG.trace("Finished {} out of {} requests", currentTotal, totalRequests);
            for (Driver client : clients) {
                currentTotal += client.execute();
            }
            if ((System.currentTimeMillis() - lastReport) > reportInterval) {
                LOG.info("Core {} reporting results", core);
                reportStream.offer(currentResult);
                currentResult = new Result(new Histogram(3));
                for (Driver client : clients) {
                    client.replaceResult(currentResult);
                }
                lastReport = System.currentTimeMillis();
            }
        }

        LOG.debug("Core {} finished", core);

        for (Driver client : clients) {
            client.finish();
        }

        currentResult.setFinished(true);
        // finished all of our requests
        reportStream.offer(currentResult);
    }
}
