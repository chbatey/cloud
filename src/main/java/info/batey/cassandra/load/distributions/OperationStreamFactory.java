package info.batey.cassandra.load.distributions;

import info.batey.cassandra.load.config.CLI;
import info.batey.cassandra.load.config.Statement;

import java.util.List;

public class OperationStreamFactory {
    public OperationStream createStream(List<Statement> statements, CLI config) {
        OperationStream[] streams = statements.stream().map(statement -> {
            VariableGenerator[] variables = statement.getVariables().stream()
                    .map(var -> {
                        VariableGenerator gen = var.getPopulation().getGenerator(var.getDefinition(), config);
                        return gen;
                    })
                    .toArray(VariableGenerator[]::new);
            return new SimpleOperationStream(statement.getText(), variables);
        }).toArray(OperationStream[]::new);

        return new CompoundOperationStream(streams);
    }
}
