package nbd.gV.repositories.reservations;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.repositories.AbstractCassandraRepository;
import nbd.gV.repositories.clients.ClientMapperBuilder;
import nbd.gV.repositories.courts.CourtMapperBuilder;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationClientsDTO;
import nbd.gV.reservations.ReservationCourtsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nbd.gV.SchemaConst.BEGIN_TIME;
import static nbd.gV.SchemaConst.CLIENT_ID;
import static nbd.gV.SchemaConst.COURT_ID;
import static nbd.gV.SchemaConst.END_TIME;
import static nbd.gV.SchemaConst.NOT_ENDED;
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
                .withColumn(CqlIdentifier.fromCql(NOT_ENDED), DataTypes.BOOLEAN)
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
                .withColumn(CqlIdentifier.fromCql(NOT_ENDED), DataTypes.BOOLEAN)
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
        //Check client
        Client client = new ClientMapperBuilder(session).build().clientDao()
                .findClient(reservation.getClient().getPersonalId());
        if (client == null) {
            throw new ReservationException("Brak podanego klienta w bazie!");
        }

        //Check court
        Court court = new CourtMapperBuilder(session).build().courtDao()
                .findCourt(reservation.getCourt().getCourtNumber());
        if (court == null) {
            throw new ReservationException("Brak podanego boiska w bazie!");
        }

        if (!court.isRented() && !client.isArchive() && !court.isArchive()) {
            court.setRented(true);
            new CourtMapperBuilder(session).build().courtDao().updateCourtRented(court);
            getDao().createClientReservation(ReservationClientsDTO.toDTO(reservation));
            getDao().createCourtReservation(ReservationCourtsDTO.toDTO(reservation));
        } else if (client.isArchive()) {
            throw new ClientException("Nie udalo sie utworzyc rezerwacji - klient jest archiwalny!");
        } else if (court.isArchive()) {
            throw new CourtException("Nie udalo sie utworzyc rezerwacji - boisko jest archiwalne!");
        } else {
            throw new ReservationException("To boisko jest aktualnie wypozyczone!");
        }
    }

    ///TODO rozszerz
    public Reservation read(UUID reservationId) {
        ReservationClientsDTO reservationClientsDTO = (ReservationClientsDTO) getDao().findReservationByUUID(reservationId);
        if (reservationClientsDTO == null) {
            return null;
        }
        //Find client
        Client client = new ClientMapperBuilder(session).build().clientDao()
                .findClientByUUID(reservationClientsDTO.getClientId());

        //Find court
        Court court = new CourtMapperBuilder(session).build().courtDao()
                .findCourtByUUID(reservationClientsDTO.getCourtId());

        Reservation readReservation = ReservationClientsDTO.fromDTO(reservationClientsDTO, client, court);
        return readReservation;
    }

    public List<Reservation> readAllByClients(SimpleStatement statement) {
        List<ReservationClientsDTO> reservationClientsDTOList =
                statement == null ? getDao().findAllReservationsByClients().all() : getDao().findAllReservationsByClientsFilter(statement).all();
        List<Reservation> reservationList = new ArrayList<>();
        for (var el : reservationClientsDTOList) {
            //Find client
            Client client = new ClientMapperBuilder(session).build().clientDao()
                    .findClientByUUID(el.getClientId());

            //Find court
            Court court = new CourtMapperBuilder(session).build().courtDao()
                    .findCourtByUUID(el.getCourtId());

            reservationList.add(ReservationClientsDTO.fromDTO(el, client, court));
        }

        return reservationList;
    }

    public List<Reservation> readAllByClients() {
        return readAllByClients(null);
    }

    public List<Reservation> readAllByCourts(SimpleStatement statement) {
        List<ReservationCourtsDTO> reservationCourtsDTOList =
                statement == null ? getDao().findAllReservationsByCourts().all() : getDao().findAllReservationsByCourtsFilter(statement).all();
        List<Reservation> reservationList = new ArrayList<>();
        for (var el : reservationCourtsDTOList) {
            //Find client
            Client client = new ClientMapperBuilder(session).build().clientDao()
                    .findClientByUUID(el.getClientId());

            //Find court
            Court court = new CourtMapperBuilder(session).build().courtDao()
                    .findCourtByUUID(el.getCourtId());

            reservationList.add(ReservationCourtsDTO.fromDTO(el, client, court));
        }

        return reservationList;
    }

    public List<Reservation> readAllByCourts() {
        return readAllByCourts(null);
    }

    //Method for updating only endTime and reservationCost
    public void update(Reservation reservation) {
        if (reservation == null || reservation.getEndTime() == null) {
            return;
        }

        //Find court
        Court court = new CourtMapperBuilder(session).build().courtDao()
                .findCourtByUUID(reservation.getCourt().getCourtId());
        if (court == null) {
            throw new ReservationException("Brak podanego boiska w bazie!");
        }

        court.setRented(false);
        new CourtMapperBuilder(session).build().courtDao().updateCourtRented(court);
        getDao().updateReservation(reservation);
    }

    public void delete(Reservation reservation) {
        if (reservation == null) {
            return;
        }

        getDao().deleteReservation(ReservationClientsDTO.toDTO(reservation));
        getDao().deleteReservation(ReservationCourtsDTO.toDTO(reservation));
    }
}
