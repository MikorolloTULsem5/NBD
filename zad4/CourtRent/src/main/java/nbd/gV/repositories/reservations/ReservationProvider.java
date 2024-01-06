package nbd.gV.repositories.reservations;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationClientsDTO;
import nbd.gV.reservations.ReservationCourtsDTO;
import nbd.gV.reservations.ReservationDTO;

import java.time.ZoneId;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.BEGIN_TIME;
import static nbd.gV.SchemaConst.CLIENT_ID;
import static nbd.gV.SchemaConst.COURT_ID;
import static nbd.gV.SchemaConst.END_TIME;
import static nbd.gV.SchemaConst.NOT_ENDED;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_CLIENT_TABLE;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_COURT_TABLE;
import static nbd.gV.SchemaConst.RESERVATION_COST;
import static nbd.gV.SchemaConst.RESERVATION_ID;

public class ReservationProvider {

    private final CqlSession session;
    private final EntityHelper<ReservationClientsDTO> reservationClientsHelper;
    private final EntityHelper<ReservationCourtsDTO> reservationCourtsHelper;

    public ReservationProvider(MapperContext context, EntityHelper<ReservationClientsDTO> reservationClientsHelper,
                               EntityHelper<ReservationCourtsDTO> reservationCourtsHelper) {
        this.session = context.getSession();
        this.reservationClientsHelper = reservationClientsHelper;
        this.reservationCourtsHelper = reservationCourtsHelper;
    }

    public ReservationDTO findReservationByUUID(UUID reservationId) {
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_CLIENT_TABLE)
                .all()
                .whereColumn(RESERVATION_ID)
                .isEqualTo(literal(reservationId))
                .allowFiltering()
                .build();
        PreparedStatement preparedSelectCourt = session.prepare(statement);
        ReservationClientsDTO reservationClientsDTO = session.execute(preparedSelectCourt.getQuery())
                .map(result -> reservationClientsHelper.get(result, true)).one();

        return reservationClientsDTO;
    }

    public PagingIterable<ReservationClientsDTO> findAllReservationsByClientsFilter(SimpleStatement statement) {
        PreparedStatement preparedSelectCourt = session.prepare(statement);
        return session.execute(preparedSelectCourt.getQuery())
                .map(result -> reservationClientsHelper.get(result, true));
    }

    public PagingIterable<ReservationCourtsDTO> findAllReservationsByCourtsFilter(SimpleStatement statement) {
        PreparedStatement preparedSelectCourt = session.prepare(statement);
        return session.execute(preparedSelectCourt.getQuery())
                .map(result -> reservationCourtsHelper.get(result, true));
    }

    public void updateReservation(Reservation reservation) {
        Update updateCL = QueryBuilder.update(RESERVATIONS_BY_CLIENT_TABLE)
                .setColumn(END_TIME, literal(reservation.getEndTime().atZone(ZoneId.systemDefault()).toInstant()))
                .setColumn(RESERVATION_COST, literal(reservation.getReservationCost()))
                .setColumn(NOT_ENDED, literal(false))
                .where(Relation.column(CLIENT_ID).isEqualTo(literal(reservation.getClient().getClientId())))
                .where(Relation.column(BEGIN_TIME).isEqualTo(literal(reservation.getBeginTime().atZone(ZoneId.systemDefault()).toInstant())))
                .where(Relation.column(RESERVATION_ID).isEqualTo(literal(reservation.getId())));

        Update updateCO = QueryBuilder.update(RESERVATIONS_BY_COURT_TABLE)
                .setColumn(END_TIME, literal(reservation.getEndTime().atZone(ZoneId.systemDefault()).toInstant()))
                .setColumn(RESERVATION_COST, literal(reservation.getReservationCost()))
                .setColumn(NOT_ENDED, literal(false))
                .where(Relation.column(COURT_ID).isEqualTo(literal(reservation.getCourt().getCourtId())))
                .where(Relation.column(BEGIN_TIME).isEqualTo(literal(reservation.getBeginTime().atZone(ZoneId.systemDefault()).toInstant())))
                .where(Relation.column(RESERVATION_ID).isEqualTo(literal(reservation.getId())));

        BatchStatement batchStatement = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(updateCL.build())
                .addStatement(updateCO.build())
                .build();
        session.execute(batchStatement);
    }
}
