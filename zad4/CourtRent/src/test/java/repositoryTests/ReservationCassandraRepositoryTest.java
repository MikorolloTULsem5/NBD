package repositoryTests;


import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import nbd.gV.SchemaConst;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;

import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.repositories.clients.ClientCassandraRepository;
import nbd.gV.repositories.courts.CourtCassandraRepository;
import nbd.gV.repositories.reservations.ReservationCassandraRepository;

import nbd.gV.reservations.Reservation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static nbd.gV.SchemaConst.BEGIN_TIME;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_CLIENT_TABLE;
import static nbd.gV.SchemaConst.RESERVATIONS_BY_COURT_TABLE;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationCassandraRepositoryTest {

    static CqlSession session;
    static ReservationCassandraRepository reservationRepository;
    static CourtCassandraRepository courtRepository;
    static ClientCassandraRepository clientRepository;
    String testClientType;

    Client testClient1;
    Client testClient2;
    Client testClient3;
    Court testCourt1;
    Court testCourt2;
    Court testCourt3;
    Court testCourt4;
    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    @BeforeAll
    static void setupTestSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandranode1", 9042))
                .addContactPoint(new InetSocketAddress("cassandranode2", 9043))
                .addContactPoint(new InetSocketAddress("cassandranode3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("admin", "adminpassword")
                .withKeyspace(CqlIdentifier.fromCql(SchemaConst.RESERVE_A_COURT_NAMESPACE))
                .build();

        session.execute("DROP TABLE IF EXISTS " + RESERVATIONS_BY_CLIENT_TABLE);
        session.execute("DROP TABLE IF EXISTS " + RESERVATIONS_BY_COURT_TABLE);

        reservationRepository = new ReservationCassandraRepository();
        courtRepository = new CourtCassandraRepository();
        clientRepository = new ClientCassandraRepository();
    }

    @AfterAll
    static void cleanTables() {
        session.execute("TRUNCATE " + RESERVATIONS_BY_CLIENT_TABLE);
        session.execute("TRUNCATE " + RESERVATIONS_BY_COURT_TABLE);
        clientRepository.readAll().forEach(clientRepository::delete);
        courtRepository.readAll().forEach(courtRepository::delete);
    }

    @BeforeEach
    void setUp() {
        cleanTables();
        testClientType = "normal";

        testClient1 = new Client("John", "Smith", "12345678901", testClientType);
        testClient2 = new Client("Eva", "Brown", "12345678902", testClientType);
        testClient3 = new Client("Adam", "Long", "12345678903", testClientType);
        clientRepository.create(testClient2);
        clientRepository.create(testClient1);
        clientRepository.create(testClient3);

        testCourt1 = new Court(1000, 100, 1);
        testCourt2 = new Court(1000, 100, 2);
        testCourt3 = new Court(1000, 100, 3);
        testCourt4 = new Court(1000, 100, 4);

        courtRepository.create(testCourt1);
        courtRepository.create(testCourt2);
        courtRepository.create(testCourt3);
        courtRepository.create(testCourt4);

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingRepository() {
        assertNotNull(reservationRepository);
        assertTrue(session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().isEmpty());
        assertTrue(session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().isEmpty());
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        reservationRepository.create(reservation);
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
        assertNotNull(reservation2);
        reservationRepository.create(reservation2);
        assertEquals(2, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(2, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        reservationRepository.create(reservation);
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        //Reserve reserved court
        Reservation reservation2 = new Reservation(testClient2, testCourt1, testTimeStart);
        assertNotNull(reservation2);
        assertThrows(ReservationException.class, () -> reservationRepository.create(reservation2));

        //No client in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                new Reservation(new Client("John", "Blade",
                        "12345678911", "normal"), testCourt3, testTimeStart)));

        //No court in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                new Reservation(testClient3, new Court(1000, 100,
                        5), testTimeStart)));

        //Archive client
        testClient3.setArchive(true);
        clientRepository.update(testClient3);
        assertThrows(ClientException.class, () -> reservationRepository.create(
                new Reservation(testClient3, testCourt3, testTimeStart)));

        //Archive court
        testCourt4.setArchive(true);
        courtRepository.update(testCourt4);
        assertThrows(CourtException.class, () -> reservationRepository.create(
                new Reservation(testClient2, testCourt4, testTimeStart)));
    }

    @Test
    void testFindingDocumentRecordsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        Reservation reservation = new Reservation(testClient1,
                testCourt1, LocalDateTime.of(2000, Month.JUNE, 13, 14, 5));
        reservationRepository.create(reservation);
        Reservation reservation2 = new Reservation(testClient2,
                testCourt2, testTimeStart);
        reservationRepository.create(reservation2);
        reservationRepository.create(new Reservation(testClient3,
                testCourt3, testTimeStart));
        assertEquals(3, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(3, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        assertEquals(reservation, reservationRepository.read(reservation.getId()));
        assertEquals(reservation2, reservationRepository.read(reservation2.getId()));

    }

    @Test
    void testFindingDocumentRecordsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        reservationRepository.create(reservation);
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        assertNull(reservationRepository.read(UUID.randomUUID()));
    }

    @Test
    void testFindingAllDocuments() {
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        reservationRepository.create(reservation);
        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
        reservationRepository.create(reservation2);
        Reservation reservation3 = new Reservation(testClient3, testCourt3, testTimeStart);
        reservationRepository.create(reservation3);
        assertEquals(3, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(3, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        //By clients
        var reservationsListByClients = reservationRepository.readAllByClients();
        assertEquals(3, reservationsListByClients.size());

        //By courts
        var reservationsListByCourts = reservationRepository.readAllByCourts();
        assertEquals(3, reservationsListByCourts.size());
    }

    @Test
    void testFindingAllDocumentsFiltered() {
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        Reservation reservation = new Reservation(testClient1, testCourt1, LocalDateTime.now());
        reservationRepository.create(reservation);
        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
        reservationRepository.create(reservation2);
        Reservation reservation3 = new Reservation(testClient3, testCourt3, testTimeStart);
        reservationRepository.create(reservation3);
        assertEquals(3, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(3, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        SimpleStatement statement = QueryBuilder.selectFrom(RESERVATIONS_BY_CLIENT_TABLE)
                .all()
                .whereColumn(BEGIN_TIME)
                .isEqualTo(literal(testTimeStart.atZone(ZoneId.systemDefault()).toInstant()))
                .allowFiltering()
                .build();

        //By clients
        var reservationsListByClients = reservationRepository.readAllByClients(statement);
        assertEquals(2, reservationsListByClients.size());

        //By courts
        var reservationsListByCourts = reservationRepository.readAllByCourts(statement);
        assertEquals(2, reservationsListByCourts.size());
    }

    @Test
    void testDeletingDocumentsInDB() {
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        reservationRepository.create(reservation);
        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
        reservationRepository.create(reservation2);
        assertEquals(2, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(2, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        reservationRepository.delete(reservation2);
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        reservationRepository.delete(reservation);
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        reservationRepository.delete(null);
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
    }

    @Test
    void testEndUpdatingDocumentsInDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        reservationRepository.create(reservation);
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        Reservation readReservation = reservationRepository.read(reservation.getId());
        assertNull(readReservation.getEndTime());
        assertEquals(0, readReservation.getReservationCost());

        reservation.endReservation(testTimeEnd);
        reservationRepository.update(reservation);

        readReservation = reservationRepository.read(reservation.getId());
        assertEquals(testTimeEnd, readReservation.getEndTime());
        assertEquals(300, readReservation.getReservationCost());
    }

    @Test
    void testEndUpdatingDocumentsInDBNegative() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(0, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());
        reservationRepository.create(reservation);
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_CLIENT_TABLE).all().size());
        assertEquals(1, session.execute("SELECT * FROM " + RESERVATIONS_BY_COURT_TABLE).all().size());

        Reservation readReservation = reservationRepository.read(reservation.getId());
        assertNull(readReservation.getEndTime());
        assertEquals(0, readReservation.getReservationCost());

        reservation.endReservation(testTimeEnd);
        reservationRepository.update(reservation);

        readReservation = reservationRepository.read(reservation.getId());
        assertEquals(testTimeEnd, readReservation.getEndTime());
        assertEquals(300, readReservation.getReservationCost());


        reservation.endReservation(testTimeEnd.plusHours(3));
        reservationRepository.update(reservation);

        readReservation = reservationRepository.read(reservation.getId());
        assertEquals(testTimeEnd, readReservation.getEndTime());
        assertEquals(300, readReservation.getReservationCost());
    }
}
