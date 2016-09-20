package info.batey.cassandra.load.schema;

import com.datastax.driver.core.Session;
import info.batey.cassandra.load.config.Config;

public class SchemaCreator {

    private Session session;

    public SchemaCreator(Session session) {

        this.session = session;
    }

    public void createSchema(Config config) {

    }
}
