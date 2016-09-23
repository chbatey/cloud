package info.batey.cassandra.load.distributions;

import info.batey.cassandra.load.Operation;

public interface OperationStream {
    Operation next();
}
