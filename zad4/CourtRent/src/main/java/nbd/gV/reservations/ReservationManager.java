package nbd.gV.reservations;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import nbd.gV.exceptions.MainException;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.repositories.reservations.ReservationCassandraRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.CLIENT_ID;
import static nbd.gV.SchemaConst.COURT_ID;
import static nbd.gV.SchemaConst.END_TIME;
import static nbd.gV.SchemaConst.NOT_ENDED;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_CLIENT_TABLE;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_COURT_TABLE;

public class ReservationManager {
    private final ReservationCassandraRepository reservationRepository;

    public ReservationManager() {
        reservationRepository = new ReservationCassandraRepository();
    }

    //Rezerwacji mozna dokonac tylko obiektami ktore juz znajduja sie w bazie danych
    public Reservation makeReservation(Client client, Court court, LocalDateTime beginTime) {
        if (client == null || court == null) {
            throw new MainException("Jeden z podanych parametrow [client/court] prowadzi do nieistniejacego obiektu!");
        }

        Reservation newReservation = new Reservation(client, court, beginTime);
        reservationRepository.create(newReservation);
        court.setRented(true);
        return newReservation;
    }

    public Reservation makeReservation(Client client, Court court) {
        return makeReservation(client, court, LocalDateTime.now());
    }

    public void returnCourt(Court court, LocalDateTime endTime) {
        if (court == null) {
            throw new MainException("Nie mozna zwrocic nieistniejacego boiska!");
        }
        Reservation reservation = getCourtReservation(court);
        reservation.endReservation(endTime);
        reservationRepository.update(reservation);
    }

    public void returnCourt(Court court) {
        returnCourt(court, LocalDateTime.now());
    }

    public List<Reservation> getAllClientReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_CLIENT_TABLE).all()
                .where(Relation.column(CLIENT_ID).isEqualTo(literal(client.getClientId())))
                .allowFiltering()
                .build();
        return reservationRepository.readAllByClients(statement);
    }

    public List<Reservation> getClientEndedReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_CLIENT_TABLE).all()
                .where(Relation.column(CLIENT_ID).isEqualTo(literal(client.getClientId())))
                .where(Relation.column(END_TIME).isGreaterThan(
                        literal(LocalDateTime.of(1900, Month.JANUARY, 1, 1 ,1, 1).atZone(ZoneId.systemDefault()).toInstant())))
                .allowFiltering()
                .build();
        return reservationRepository.readAllByClients(statement);
    }

    public Reservation getCourtReservation(Court court) {
        if (court == null) {
            throw new MainException("Nie istniejace boisko nie moze posiadac rezerwacji!");
        }
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_COURT_TABLE).all()
                .where(Relation.column(COURT_ID).isEqualTo(literal(court.getCourtId())))
                .where(Relation.column(NOT_ENDED).isEqualTo(literal(true)))
                .allowFiltering()
                .build();
        return reservationRepository.readAllByCourts(statement).get(0);
    }

    ///TODO test
    public List<Reservation> getCourtEndedReservations(Court court) {
        if (court == null) {
            throw new MainException("Nie istniejace boisko nie moze posiadac rezerwacji!");
        }
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_COURT_TABLE).all()
                .where(Relation.column(COURT_ID).isEqualTo(literal(court.getCourtId())))
                .where(Relation.column(END_TIME).isGreaterThan(
                        literal(LocalDateTime.of(1900, Month.JANUARY, 1, 1 ,1, 1).atZone(ZoneId.systemDefault()).toInstant())))
                .allowFiltering()
                .build();
        return reservationRepository.readAllByCourts(statement);
    }

    public double checkClientReservationBalance(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna obliczyc salda dla nieistniejacego klienta!");
        }
        double sum = 0;
        List<Reservation> reservationList = getClientEndedReservations(client);
        for (Reservation reservation : reservationList) {
            sum += reservation.getReservationCost();
        }
        return sum;
    }

    public List<Reservation> getAllCurrentReservations() {
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_CLIENT_TABLE).all()
                .where(Relation.column(NOT_ENDED).isEqualTo(literal(true)))
                .allowFiltering()
                .build();
        return reservationRepository.readAllByCourts(statement);
    }

    public List<Reservation> getAllArchiveReservations() {
        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_CLIENT_TABLE).all()
                .where(Relation.column(END_TIME).isGreaterThan(
                        literal(LocalDateTime.of(1900, Month.JANUARY, 1, 1 ,1, 1).atZone(ZoneId.systemDefault()).toInstant())))
                .allowFiltering()
                .build();
        return reservationRepository.readAllByCourts(statement);
    }

    public Reservation getReservationByID(UUID uuid) {
        return reservationRepository.read(uuid);
    }
}
