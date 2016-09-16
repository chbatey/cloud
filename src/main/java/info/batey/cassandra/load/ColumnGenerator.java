package info.batey.cassandra.load;

public interface ColumnGenerator<T> {
    T next();
}
