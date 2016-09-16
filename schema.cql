CREATE KEYSPACE test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '3'}  AND durable_writes = true;

CREATE TABLE test.kv (
    key text PRIMARY KEY,
    value text
);