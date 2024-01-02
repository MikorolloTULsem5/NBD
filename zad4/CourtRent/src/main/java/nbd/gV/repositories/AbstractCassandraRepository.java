package nbd.gV.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import lombok.Getter;
import nbd.gV.SchemaConst;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public class AbstractCassandraRepository implements AutoCloseable {
    @Getter
    private static CqlSession session;

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

    public void createClientsTable() {
        SimpleStatement createClients = SchemaBuilder.createTable(CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("personal_id"), DataTypes.TEXT)
                .withClusteringColumn(CqlIdentifier.fromCql("client_type_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("archive"), DataTypes.BOOLEAN)
                .withClusteringOrder(CqlIdentifier.fromCql("client_type_name"), ClusteringOrder.ASC)
                .build();
        session.execute(createClients);
    }

    @Override
    public void close() throws Exception {
        session.close();
    }
}
