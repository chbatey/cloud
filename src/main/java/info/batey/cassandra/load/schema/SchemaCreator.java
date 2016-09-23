package info.batey.cassandra.load.schema;

import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.Uninterruptibles;
import info.batey.cassandra.load.config.Profile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.concurrent.TimeUnit.*;


public class SchemaCreator {

    private final static Logger log = LogManager.getLogger(SchemaCreator.class);
    private Session session;

    public SchemaCreator(Session session) {

        this.session = session;
    }

    public void createSchema(Profile config) {
        config.getKeyspaces().forEach(keyspace -> session.execute(keyspace.getDefinition()));

        waitForSchema();

        config.getTables().forEach(table -> {
            session.execute("use " + table.getKeyspace());
            session.execute(table.getDefinition());
        });

        waitForSchema();
    }

    private void waitForSchema() {
        while (!session.getCluster().getMetadata().checkSchemaAgreement()) {
            log.info("Schema not in agreement. Sleeping before creating tables...");
            Uninterruptibles.sleepUninterruptibly(2, SECONDS);
        }
    }
}
