package nbd.gV.repositories.clients;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import nbd.gV.clients.Client;
import nbd.gV.repositories.AbstractCassandraRepository;

import java.util.UUID;

import static nbd.gV.SchemaConst.ARCHIVE;
import static nbd.gV.SchemaConst.CLIENTS_TABLE;
import static nbd.gV.SchemaConst.CLIENT_ID;
import static nbd.gV.SchemaConst.CLIENT_TYPE_NAME;
import static nbd.gV.SchemaConst.FIRST_NAME;
import static nbd.gV.SchemaConst.LAST_NAME;
import static nbd.gV.SchemaConst.PERSONAL_ID;

public class ClientCassandraRepository extends AbstractCassandraRepository {
    public ClientCassandraRepository() {
        initSession();
        addKeyspace();

        SimpleStatement createClients = SchemaBuilder.createTable(CLIENTS_TABLE)
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql(PERSONAL_ID), DataTypes.TEXT)
                .withClusteringColumn(CqlIdentifier.fromCql(CLIENT_TYPE_NAME), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql(CLIENT_ID), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql(FIRST_NAME), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql(LAST_NAME), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql(ARCHIVE), DataTypes.BOOLEAN)
                .withClusteringOrder(CqlIdentifier.fromCql(CLIENT_TYPE_NAME), ClusteringOrder.ASC)
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
