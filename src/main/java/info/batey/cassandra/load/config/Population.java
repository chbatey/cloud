package info.batey.cassandra.load.config;


import info.batey.cassandra.load.distributions.fixed.FixedTextVariable;
import info.batey.cassandra.load.distributions.SingleTextVariable;
import info.batey.cassandra.load.distributions.VariableGenerator;

import java.util.function.BiFunction;

public enum Population {
    single((variable, cli) -> {
        return new SingleTextVariable(variable.getDefinition().get("value").toString());
    }),
    fixed((variable, cli) -> {
        return new FixedTextVariable(Integer.valueOf(variable.getDefinition().get("number").toString()) / cli.cores);
    });

    private final BiFunction<Variable, CloadCli.ProfileCommand, VariableGenerator> generate;

    Population(BiFunction<Variable, CloadCli.ProfileCommand, VariableGenerator> generate) {
        this.generate = generate;
    }

    public VariableGenerator getGenerator(Variable variable, CloadCli.ProfileCommand nrCores) {
        return generate.apply(variable, nrCores);
    }
}
