package info.batey.cassandra.load.drivers;

import com.datastax.driver.core.Session;
import info.batey.cassandra.load.Request;

public interface Operation {
    Request execute(Session session);
}
