package nbd.gV.repositories.clients;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import nbd.gV.clients.Client;
import nbd.gV.repositories.AbstractCassandraRepository;

import java.util.UUID;

public class ClientCassandraRepository extends AbstractCassandraRepository {
    public ClientCassandraRepository() {
        initSession();
        addKeyspace();

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

    /*----------------------------------------------CRUD-------------------------------------------------------*/

    public void create(Client client) {
        ClientMapper clientMapper = new ClientMapperBuilder(session).build();
        ClientDao clientDao = clientMapper.clientDao();

        clientDao.create(client);
    }

    public Client read(String personalId) {
        ClientMapper clientMapper = new ClientMapperBuilder(session).build();
        ClientDao clientDao = clientMapper.clientDao();

        return clientDao.findClient(personalId);
    }

    public Client readByUUID(UUID clientId) {
        ClientMapper clientMapper = new ClientMapperBuilder(session).build();
        ClientDao clientDao = clientMapper.clientDao();

        return clientDao.findClientByUUID(clientId);
    }

    public Client readByUUID(String clientId) {
        return readByUUID(UUID.fromString(clientId));
    }
}
