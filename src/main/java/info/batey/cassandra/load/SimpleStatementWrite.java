package info.batey.cassandra.load;

import com.datastax.driver.core.ResultSetFuture;

import java.util.List;

import static java.util.Arrays.asList;

public class SimpleStatementWrite implements OperationStream {

    private final String statement;
    private final List<ColumnGenerator<?>> cols;

    public SimpleStatementWrite(String statement, ColumnGenerator... cols) {
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
