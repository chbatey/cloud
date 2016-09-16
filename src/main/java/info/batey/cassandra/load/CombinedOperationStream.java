package info.batey.cassandra.load;

public class CombinedOperationStream implements OperationStream {

    private final OperationStream[] streams;
    private int i = 0;

    public CombinedOperationStream(OperationStream... streams) {
        this.streams = streams;
    }

    @Override
    public Operation next() {
        OperationStream stream = streams[i];
        i = (i+1) % streams.length;
        return stream.next();
    }
}
