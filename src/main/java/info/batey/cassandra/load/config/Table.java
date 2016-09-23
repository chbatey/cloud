package info.batey.cassandra.load.config;

public class Table {
    private String name;
    private String definition;
    private String keyspace;

    public Table(String name, String definition, String keyspace) {
        this.name = name;
        this.definition = definition;
        this.keyspace = keyspace;
    }

    // for yaml parsing
    public Table() {
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

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", definition='" + definition + '\'' +
                ", keyspace='" + keyspace + '\'' +
                '}';
    }
}
