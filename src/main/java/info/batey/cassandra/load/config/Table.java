package info.batey.cassandra.load.config;

public class Table {
    private String name;
    private String definition;

    public void setName(String name) {
        this.name = name;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", definition='" + definition + '\'' +
                '}';
    }
}
