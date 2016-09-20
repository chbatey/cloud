package info.batey.cassandra.load.config;

import java.util.List;

public class Config {
    private List<Keyspace> keyspaces;
    private List<Table> tables;


    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void setKeyspaces(List<Keyspace> keyspaces) {
        this.keyspaces = keyspaces;
    }

    @Override
    public String toString() {
        return "Config{" +
                "keyspaces=" + keyspaces +
                ", tables=" + tables +
                '}';
    }
}
