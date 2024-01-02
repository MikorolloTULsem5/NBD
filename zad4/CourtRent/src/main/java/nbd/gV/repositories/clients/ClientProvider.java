package nbd.gV.repositories.clients;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;

import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import nbd.gV.clients.Client;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.CLIENTS_TABLE;

public class ClientProvider {

    private final CqlSession session;
    private final EntityHelper<Client> clientHelper;

    public ClientProvider(MapperContext context, EntityHelper<Client> clientHelper) {
        this.session = context.getSession();
        this.clientHelper = clientHelper;
    }

    public Client findClientByUUID(UUID clientId) {
        SimpleStatement statement = QueryBuilder.selectFrom(CLIENTS_TABLE)
                .all()
                .whereColumn("client_id")
                .isEqualTo(literal(clientId))
                .allowFiltering()
                .build();
        PreparedStatement preparedSelectUser = session.prepare(statement);
        return session
                .execute(preparedSelectUser.getQuery())
                .map(result -> clientHelper.get(result, true)).one();
    }
}
