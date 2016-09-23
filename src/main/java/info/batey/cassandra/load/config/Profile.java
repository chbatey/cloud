package info.batey.cassandra.load.config;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Profile {
    private List<Keyspace> keyspaces;
    private List<Table> tables;
    private List<Statement> statements;
    private Map<String, Integer> scenario;

    public Map<String, Integer> getScenario() {
        return scenario;
    }

    public void setScenario(Map<String, Integer> scenario) {
        this.scenario = scenario;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void setKeyspaces(List<Keyspace> keyspaces) {
        this.keyspaces = keyspaces;
    }

    public List<Keyspace> getKeyspaces() {
        return keyspaces;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "keyspaces=" + keyspaces +
                ", tables=" + tables +
                ", statements=" + statements +
                '}';
    }

    /**
     * Throws if invalid.
     */
    public void validate() {
        tables.forEach(table -> {
            if (table.getKeyspace() == null)
                throw new RuntimeException("Must specify keyspace for table " + table.getName());
        });

        // todo - change to use a result class
    }


    public static Profile parse(String fileName) throws FileNotFoundException {
        Constructor ctr = new Constructor(Profile.class);
        TypeDescription configDes = new TypeDescription(Profile.class);
        configDes.putListPropertyType("keyspaces", Keyspace.class);
        configDes.putListPropertyType("tables", Table.class);
        configDes.putListPropertyType("statements", Statement.class);
        configDes.putMapPropertyType("scenario", String.class, Integer.class);

        TypeDescription configStatement = new TypeDescription(Statement.class);
        configStatement.putListPropertyType("variables", Variable.class);

        TypeDescription typeVariable = new TypeDescription(Variable.class);
        typeVariable.putMapPropertyType("definition", String.class, Object.class);


        ctr.addTypeDescription(configDes);
        ctr.addTypeDescription(configStatement);
        ctr.addTypeDescription(typeVariable);

        Yaml yaml = new Yaml(ctr);

        InputStream input = new FileInputStream(new File(fileName));
        return (Profile) yaml.load(input);
    }
}
