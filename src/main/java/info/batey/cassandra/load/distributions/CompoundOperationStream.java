package info.batey.cassandra.load.distributions;

import info.batey.cassandra.load.Operation;
import info.batey.cassandra.load.distributions.OperationStream;

public class CompoundOperationStream implements OperationStream {

    private final OperationStream[] streams;
    private int i = 0;

    public CompoundOperationStream(OperationStream... streams) {
        this.streams = streams;
    }

    @Override
    public Operation next() {
        OperationStream stream = streams[i];
        i = (i+1) % streams.length;
        return stream.next();
    }
}
