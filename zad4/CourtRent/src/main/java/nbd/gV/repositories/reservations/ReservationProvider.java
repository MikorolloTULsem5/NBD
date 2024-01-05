package nbd.gV.repositories.reservations;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import nbd.gV.reservations.ReservationClientsDTO;
import nbd.gV.reservations.ReservationDTO;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_CLIENT_TABLE;
import static nbd.gV.SchemaConst.RESERVATION_ID;

public class ReservationProvider {

    private final CqlSession session;
    private final EntityHelper<ReservationClientsDTO> reservationClientsHelper;

    public ReservationProvider(MapperContext context, EntityHelper<ReservationClientsDTO> reservationClientsHelper) {
        this.session = context.getSession();
        this.reservationClientsHelper = reservationClientsHelper;
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

}
