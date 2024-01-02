package nbd.gV.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;

import nbd.gV.SchemaConst;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public abstract class AbstractCassandraRepository implements AutoCloseable {
    protected static CqlSession session;

    public void initSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandranode1", 9042))
                .addContactPoint(new InetSocketAddress("cassandranode2", 9043))
                .addContactPoint(new InetSocketAddress("cassandranode3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("admin", "adminpassword")
                .withKeyspace(CqlIdentifier.fromCql(SchemaConst.RESERVE_A_COURT_NAMESPACE))
                .build();
    }

    public void addKeyspace() {
        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql(SchemaConst.RESERVE_A_COURT_NAMESPACE))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        session.execute(createKeyspace);
    }

    @Override
    public void close() throws Exception {
        session.close();
    }
}
