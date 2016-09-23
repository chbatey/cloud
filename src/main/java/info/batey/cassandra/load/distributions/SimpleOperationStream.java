package info.batey.cassandra.load.distributions;

import com.datastax.driver.core.ResultSetFuture;
import info.batey.cassandra.load.Operation;
import info.batey.cassandra.load.Request;

import java.util.List;

import static java.util.Arrays.asList;

public class SimpleOperationStream implements OperationStream {

    private final String statement;
    private final List<VariableGenerator<?>> cols;

    public SimpleOperationStream(String statement, VariableGenerator... cols) {
        this.statement = statement;
        this.cols = asList(cols);
    }

    @Override
    public Operation next() {
        return session -> {
            Object[] args = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                args[i] = cols.get(i).next();
            }
            ResultSetFuture resultSetFuture = session.executeAsync(statement, args);
            return new Request(resultSetFuture);
        };
    }
}
