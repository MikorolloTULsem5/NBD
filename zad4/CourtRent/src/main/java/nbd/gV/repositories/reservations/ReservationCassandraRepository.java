package nbd.gV.repositories.reservations;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import nbd.gV.repositories.AbstractCassandraRepository;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationClientsDTO;
import nbd.gV.reservations.ReservationCourtsDTO;

import static nbd.gV.SchemaConst.BEGIN_TIME;
import static nbd.gV.SchemaConst.CLIENT_ID;
import static nbd.gV.SchemaConst.COURT_ID;
import static nbd.gV.SchemaConst.END_TIME;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_CLIENT_TABLE;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_COURT_TABLE;
import static nbd.gV.SchemaConst.RESERVATION_COST;
import static nbd.gV.SchemaConst.RESERVATION_ID;

public class ReservationCassandraRepository extends AbstractCassandraRepository {
    public ReservationCassandraRepository() {
        initSession();
        addKeyspace();

        SimpleStatement createReservationsByClient = SchemaBuilder.createTable(RESERVATIONS_BY_CLIENT_TABLE)
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql(CLIENT_ID), DataTypes.UUID)
                .withClusteringColumn(CqlIdentifier.fromCql(BEGIN_TIME), DataTypes.TIMESTAMP)
                .withClusteringColumn(CqlIdentifier.fromCql(RESERVATION_ID), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql(END_TIME), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql(COURT_ID), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql(RESERVATION_COST), DataTypes.DOUBLE)
                .withClusteringOrder(CqlIdentifier.fromCql(BEGIN_TIME), ClusteringOrder.ASC)
                .build();
        session.execute(createReservationsByClient);

        SimpleStatement createReservationsByCourt = SchemaBuilder.createTable(RESERVATIONS_BY_COURT_TABLE)
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql(COURT_ID), DataTypes.UUID)
                .withClusteringColumn(CqlIdentifier.fromCql(BEGIN_TIME), DataTypes.TIMESTAMP)
                .withClusteringColumn(CqlIdentifier.fromCql(RESERVATION_ID), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql(END_TIME), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql(CLIENT_ID), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql(RESERVATION_COST), DataTypes.DOUBLE)
                .withClusteringOrder(CqlIdentifier.fromCql(BEGIN_TIME), ClusteringOrder.ASC)
                .build();
        session.execute(createReservationsByCourt);
    }

    protected ReservationDao getDao() {
        ReservationMapper reservationMapper = new ReservationMapperBuilder(session).build();
        return reservationMapper.reservationDao();
    }

    /*----------------------------------------------CRUD-------------------------------------------------------*/

    public void create(Reservation reservation) {
        getDao().createClientReservation(ReservationClientsDTO.toDTO(reservation));
        getDao().createCourtReservation(ReservationCourtsDTO.toDTO(reservation));
    }
}
