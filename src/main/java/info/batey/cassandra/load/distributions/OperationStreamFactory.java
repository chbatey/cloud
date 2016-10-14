package info.batey.cassandra.load.distributions;

import info.batey.cassandra.load.config.CloadCli;
import info.batey.cassandra.load.config.Statement;
import info.batey.cassandra.load.config.Variable;

import java.util.List;

public class OperationStreamFactory {
    public OperationStream createStream(List<Statement> statements, CloadCli.ProfileCommand config) {
        OperationStream[] streams = statements.stream().map(statement -> {
            VariableGenerator[] variables = statement.getVariables().stream()
                    .map((Variable var) -> {
                        VariableGenerator gen = var.getPopulation().getGenerator(var, config);
                        return gen;
                    })
                    .toArray(VariableGenerator[]::new);
            return new SimpleOperationStream(statement.getText(), variables);
        }).toArray(OperationStream[]::new);

        return new CompoundOperationStream(streams);
    }
}
