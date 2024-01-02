package nbd.gV.repositories.clients;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import nbd.gV.clients.Client;

import java.util.UUID;

@Dao
public interface ClientDao {
    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void create(Client client);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    Client findClient(String personalId);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ClientProvider.class, entityHelpers = {Client.class}, providerMethod = "findClientByUUID")
    Client findClientByUUID(UUID clientId);
}
