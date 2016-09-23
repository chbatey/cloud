package info.batey.cassandra.load.schema;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import info.batey.cassandra.load.config.Profile;
import info.batey.cassandra.load.config.Keyspace;
import info.batey.cassandra.load.config.Table;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;

public class SchemaCreatorTest {

    private static Cluster cluster;
    private static Session session;

    private SchemaCreator underTest;

    @BeforeClass
    public static void setup() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        cluster = Cluster.builder()
                .addContactPoint(EmbeddedCassandraServerHelper.getHost())
                .withPort(EmbeddedCassandraServerHelper.getNativeTransportPort())
                .build();
        session = cluster.connect();
    }

    @Before
    public void setUp() throws Exception {
        underTest = new SchemaCreator(session);
    }

    @Test
    public void schemaShouldBeCreated() throws Exception {
        Profile profile = new Profile();
        String keyspaceName = "chris";
        String tableName = "chris_table";
        List<Keyspace> keyspaces = singletonList(new Keyspace(keyspaceName, "CREATE KEYSPACE chris WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }"));
        List<Table> tables =  singletonList(new Table(tableName, "CREATE TABLE chris_table ( key text PRIMARY KEY, value text )", keyspaceName));
        profile.setKeyspaces(keyspaces);
        profile.setTables(tables);

        underTest.createSchema(profile);

        Row keyspaceRow = session.execute("select * from system.schema_keyspaces where keyspace_name = ?", keyspaceName).one();
        assertNotNull("Keyspace should exist", keyspaceRow);

        Row tableRow = session.execute("select * from system.schema_columnfamilies where keyspace_name = ? and columnfamily_name = ?", keyspaceName, tableName).one();
        assertNotNull("Table should exist", tableRow);
    }
}