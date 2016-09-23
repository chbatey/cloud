package info.batey.cassandra.load.distributions;

public interface VariableGenerator<T> {
    T next();
}
