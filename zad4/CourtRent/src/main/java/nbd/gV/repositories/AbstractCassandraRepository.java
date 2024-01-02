package nbd.gV.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;

import java.net.InetSocketAddress;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public class AbstractCassandraRepository implements AutoCloseable {
    private static CqlSession session;

    public void initSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandranode1", 9042))
                .addContactPoint(new InetSocketAddress("cassandranode2", 9043))
                .addContactPoint(new InetSocketAddress("cassandranode3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("admin", "adminpassword")
                .withKeyspace(CqlIdentifier.fromCql("rent_a_car"))
                .build();
    }

    public void addKeyspace() {
        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql("rent_a_car"))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        session.execute(createKeyspace);
    }

    public void createClientsTable() {
        SimpleStatement createClients = SchemaBuilder.createTable(CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("personalId"), DataTypes.TEXT)
                .withClusteringColumn(CqlIdentifier.fromCql("clientTypeName"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("clientId"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("firstName"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("lastName"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("archive"), DataTypes.BOOLEAN)
                .withClusteringOrder(CqlIdentifier.fromCql("clientTypeName"), ClusteringOrder.ASC)
                .build();
        session.execute(createClients);
    }

    @Override
    public void close() throws Exception {
        session.close();
    }
}
