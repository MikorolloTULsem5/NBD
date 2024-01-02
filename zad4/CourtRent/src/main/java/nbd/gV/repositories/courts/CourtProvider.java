package nbd.gV.repositories.courts;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;

import nbd.gV.courts.Court;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.COURTS_TABLE;
import static nbd.gV.SchemaConst.COURT_ID;

public class CourtProvider {

    private final CqlSession session;
    private final EntityHelper<Court> courtHelper;

    public CourtProvider(MapperContext context, EntityHelper<Court> courtHelper) {
        this.session = context.getSession();
        this.courtHelper = courtHelper;
    }

    public Court findCourtByUUID(UUID courtId) {
        SimpleStatement statement = QueryBuilder.selectFrom(COURTS_TABLE)
                .all()
                .whereColumn(COURT_ID)
                .isEqualTo(literal(courtId))
                .allowFiltering()
                .build();
        PreparedStatement preparedSelectCourt = session.prepare(statement);
        return session
                .execute(preparedSelectCourt.getQuery())
                .map(result -> courtHelper.get(result, true)).one();
    }
}
