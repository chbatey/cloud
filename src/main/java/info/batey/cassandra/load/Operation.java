package info.batey.cassandra.load;

import com.datastax.driver.core.Session;

public interface Operation {
    Request execute(Session session);
}
