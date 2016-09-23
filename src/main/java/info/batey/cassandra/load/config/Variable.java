package info.batey.cassandra.load.config;

import java.util.Map;

public class Variable {
    private Type type;
    private Population population;

    // differs based on the population
    private Map<String, Object> definition;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public Map<String, Object> getDefinition() {
        return definition;
    }

    public void setDefinition(Map<String, Object> definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "type=" + type +
                ", population=" + population +
                ", definition=" + definition +
                '}';
    }
}
