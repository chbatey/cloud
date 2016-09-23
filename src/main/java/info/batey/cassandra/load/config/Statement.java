package info.batey.cassandra.load.config;

import java.util.List;

public class Statement {
    String name;
    String text;
    List<Variable> variables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "Statement{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", variables=" + variables +
                '}';
    }
}
