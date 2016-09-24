package info.batey.cassandra.load.config;


import info.batey.cassandra.load.distributions.FixedTextVariable;
import info.batey.cassandra.load.distributions.SingleTextVariable;
import info.batey.cassandra.load.distributions.VariableGenerator;

import java.util.Map;
import java.util.function.BiFunction;

public enum Population {
    single((def, cli) -> new SingleTextVariable(def.get("value").toString())),
    fixed((def, cli) -> new FixedTextVariable(Integer.valueOf(def.get("number").toString()) / cli.cores));

    private final BiFunction<Map<String, Object>, CloadCli.ProfileCommand, VariableGenerator> generate;

    Population(BiFunction<Map<String, Object>, CloadCli.ProfileCommand, VariableGenerator> generate) {
        this.generate = generate;
    }

    public VariableGenerator getGenerator(Map<String, Object> definition, CloadCli.ProfileCommand nrCores) {
        return generate.apply(definition, nrCores);
    }
}
