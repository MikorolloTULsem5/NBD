package nbd.gV.repositories.clients;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;
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
    @QueryProvider(providerClass = ClientProvider.class, entityHelpers = {Client.class},
            providerMethod = "findClientByUUID")
    Client findClientByUUID(UUID clientId);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    PagingIterable<Client> findAllClients();

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Update
    void updateClient(Client user);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void deleteClient(Client user);
}
