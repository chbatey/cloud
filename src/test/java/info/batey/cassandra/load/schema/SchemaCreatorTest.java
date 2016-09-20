package info.batey.cassandra.load.schema;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.collect.Lists;
import info.batey.cassandra.load.config.Config;
import info.batey.cassandra.load.config.Keyspace;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.junit.Assert.*;

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
    public void test() throws Exception {
        Config config = new Config();
        List<Keyspace> keyspaces = Collections.singletonList(new Keyspace("chris", "CREATE KEYSPACE chris WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };"));
        config.setKeyspaces(keyspaces);
    }
}