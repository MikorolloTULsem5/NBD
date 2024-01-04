package nbd.gV.repositories.courts;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;

import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import jnr.ffi.annotations.In;
import nbd.gV.courts.Court;

import java.time.Instant;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.ARCHIVE;
import static nbd.gV.SchemaConst.AREA;
import static nbd.gV.SchemaConst.BASE_COST;
import static nbd.gV.SchemaConst.COURTS_TABLE;
import static nbd.gV.SchemaConst.COURT_ID;
import static nbd.gV.SchemaConst.COURT_NUMBER;
import static nbd.gV.SchemaConst.RENTED;

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

    public void updateCourtRented(Court court) {
        SimpleStatement deleteCourt = QueryBuilder.deleteFrom(COURTS_TABLE)
                .where(Relation.column(COURT_NUMBER).isEqualTo(literal(court.getCourtNumber())))
                .where(Relation.column(RENTED).isEqualTo(literal(!court.isRented())))
                .build().setQueryTimestamp(Instant.now().getNano());
        SimpleStatement insertCourt = QueryBuilder.insertInto(COURTS_TABLE)
                .value(COURT_NUMBER, literal(court.getCourtNumber()))
                .value(RENTED, literal(court.isRented()))
                .value(ARCHIVE, literal(court.isArchive()))
                .value(AREA, literal(court.getArea()))
                .value(BASE_COST, literal(court.getBaseCost()))
                .value(COURT_ID, literal(court.getCourtId())).build().setQueryTimestamp(Instant.now().getNano());

        BatchStatement batchStatement = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(deleteCourt)
                .addStatement(insertCourt)
                .build();
        session.execute(batchStatement);
    }
}
