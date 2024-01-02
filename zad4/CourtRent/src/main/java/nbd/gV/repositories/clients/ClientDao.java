package nbd.gV.repositories.clients;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import nbd.gV.clients.Client;

import java.util.UUID;

@Dao
public interface ClientDao {
    @Insert
    @StatementAttributes(consistencyLevel = "QUORUM")
    void create(Client client);

//    @QueryProvider(providerClass = FindClientQueryProvider.class, entityHelpers = {Client.class})
//    Client findClient(UUID id);
}
