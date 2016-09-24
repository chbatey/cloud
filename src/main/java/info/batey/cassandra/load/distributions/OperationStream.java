package info.batey.cassandra.load.distributions;

import info.batey.cassandra.load.drivers.Operation;

public interface OperationStream {
    Operation next();
}
