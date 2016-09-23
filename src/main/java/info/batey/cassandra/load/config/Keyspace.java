package info.batey.cassandra.load.config;

public class Keyspace {
    private String name;
    private String definition;

    public Keyspace(String name, String definition) {
        this.name = name;
        this.definition = definition;
    }

    // for yaml parsing
    public Keyspace() {
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "Keyspace{" +
                "name='" + name + '\'' +
                ", definition='" + definition + '\'' +
                '}';
    }
}
