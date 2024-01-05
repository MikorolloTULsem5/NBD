package repositoryTests;


import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import nbd.gV.SchemaConst;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;

import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.repositories.clients.ClientCassandraRepository;
import nbd.gV.repositories.courts.CourtCassandraRepository;
import nbd.gV.repositories.reservations.ReservationCassandraRepository;

import nbd.gV.repositories.reservations.ReservationMapper;
import nbd.gV.reservations.Reservation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

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

//    @Test
//    void testFindingAllDocuments() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        ReservationMapper reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
//                testCourt1, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper1));
//        ReservationMapper reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
//                testCourt2, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper2));
//        ReservationMapper reservationMapper3 = ReservationMapper.toMongoReservation(new Reservation(testClient3,
//                testCourt3, testTimeStart));
//        assertTrue(reservationRepository.create(reservationMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var reservationsList = reservationRepository.readAll();
//        assertEquals(3, reservationsList.size());
//        assertEquals(reservationMapper1, reservationsList.get(0));
//        assertEquals(reservationMapper2, reservationsList.get(1));
//        assertEquals(reservationMapper3, reservationsList.get(2));
//    }
//
//    @Test
//    void testDeletingDocumentsInDB() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        var reservationMapper1 =
//                ReservationMapper.toMongoReservation(new Reservation(testClient1, testCourt1, testTimeStart));
//        reservationRepository.create(reservationMapper1);
//        var reservationMapper2 =
//                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt2, testTimeStart));
//        reservationRepository.create(reservationMapper2);
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//
//        reservationRepository.delete(UUID.fromString(reservationMapper2.getId()));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        reservationRepository.delete(UUID.fromString(reservationMapper1.getId()));
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertThrows(NullPointerException.class, () -> reservationRepository.delete(null));
//        assertFalse(reservationRepository.delete(UUID.randomUUID()));
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//    }
//
//    @Test
//    void testClassicUpdatingDocumentsInDBPositive() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertEquals(testClient1.getClientId().toString(),
//                reservationRepository.readByUUID(reservation.getId()).getClientId());
//        assertTrue(reservationRepository.update(reservation.getId(), "clientid",
//                testClient2.getClientId().toString()));
//        assertEquals(testClient2.getClientId().toString(),
//                reservationRepository.readByUUID(reservation.getId()).getClientId());
//    }
//
//    @Test
//    void testClassicUpdatingDocumentsInDBNegative() {
//        Reservation reservation = new Reservation(testClient2, testCourt2, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertThrows(MyMongoException.class, () -> reservationRepository.update(reservation.getId(),
//                "_id", UUID.randomUUID().toString()));
//        assertFalse(reservationRepository.update(UUID.randomUUID(), "clientid",
//                testClient2.getClientId().toString()));
//    }
//
//    @Test
//    void testEndUpdatingDocumentsInDBPositive() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertNull(reservationRepository.readByUUID(reservation.getId()).getEndTime());
//        assertEquals(0, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
//        reservationRepository.update(testCourt1, testTimeEnd);
//        assertEquals(testTimeEnd, reservationRepository.readByUUID(reservation.getId()).getEndTime());
//        assertEquals(300, reservationRepository.readByUUID(reservation.getId()).getReservationCost());
//    }
//
//    @Test
//    void testEndUpdatingDocumentsInDBNegative() {
//        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
//        assertNotNull(reservation);
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        reservationRepository.update(testCourt1, testTimeEnd);
//        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt1, testTimeEnd));
//    }
}
