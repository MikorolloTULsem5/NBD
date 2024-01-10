package managersTests;

import nbd.gV.clients.*;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.repositories.clients.ClientCassandraRepository;
import nbd.gV.repositories.courts.CourtCassandraRepository;
import nbd.gV.repositories.reservations.ReservationCassandraRepository;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationManagerTest {

    static final ReservationCassandraRepository reservationRepository = new ReservationCassandraRepository();
    static final ClientCassandraRepository clientRepository = new ClientCassandraRepository();
    static final CourtCassandraRepository courtRepository = new CourtCassandraRepository();

    static final ReservationManager rm = new ReservationManager();

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
    @AfterAll
    static void cleanDB() {
        reservationRepository.readAllByClients().forEach(reservationRepository::delete);
        reservationRepository.readAllByCourts().forEach(reservationRepository::delete);
        clientRepository.readAll().forEach(clientRepository::delete);
        courtRepository.readAll().forEach(courtRepository::delete);
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = "normal";

        testClient1 = new Client("John", "Smith", "12345678901", testClientType);
        testClient2 = new Client("Eva", "Brown", "12345678902", testClientType);
        testClient3 = new Client("Adam", "Long", "12345678903", testClientType);
        clientRepository.create(testClient1);
        clientRepository.create(testClient2);
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
    void testCreatingReservationManager() {
        assertNotNull(rm);
    }

    @Test
    void testMakingReservation() {
        assertFalse(testCourt1.isRented());

        Reservation newReservation = rm.makeReservation(testClient1, testCourt1, testTimeStart);

        assertEquals(rm.getAllCurrentReservations().size(), 1);
        assertEquals(newReservation, rm.getReservationByID(newReservation.getId()));
        assertTrue(testCourt1.isRented());

        assertFalse(testCourt2.isRented());
        Reservation newReservation2 = rm.makeReservation(testClient1, testCourt2);

        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertEquals(newReservation2, rm.getReservationByID(newReservation2.getId()));
        assertTrue(testCourt2.isRented());

        assertThrows(ReservationException.class, () -> rm.makeReservation(testClient1, testCourt1, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertTrue(testCourt1.isRented());

        testClient2.setArchive(true);
        clientRepository.update(testClient2);
        assertFalse(testCourt3.isRented());
        assertThrows(ClientException.class, () -> rm.makeReservation(testClient2, testCourt3, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertFalse(testCourt3.isRented());

        testCourt4.setArchive(true);
        courtRepository.update(testCourt4);
        assertFalse(testCourt4.isRented());
        assertThrows(CourtException.class, () -> rm.makeReservation(testClient1, testCourt4, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertFalse(testCourt4.isRented());

        assertThrows(MainException.class, () -> rm.makeReservation(null, testCourt4, testTimeStart));
        assertFalse(testCourt4.isRented());
        assertEquals(rm.getAllCurrentReservations().size(), 2);

        assertThrows(MainException.class, () -> rm.makeReservation(testClient1, null, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
    }

    @Test
    void testCreatingReservationManagerWithNullDate() {
        assertEquals(0, rm.getAllCurrentReservations().size());
        Reservation newReservation = rm.makeReservation(testClient1, testCourt1);
        var reservations = rm.getAllCurrentReservations();
        assertEquals(1, reservations.size());
        assertEquals(newReservation, reservations.get(0));
    }

    @Test
    void testEndReservation() {
        rm.makeReservation(testClient1,testCourt1,testTimeStart);
        rm.makeReservation(testClient2,testCourt2,testTimeStart);

        assertEquals(0, rm.getAllArchiveReservations().size());
        assertEquals(2, rm.getAllCurrentReservations().size());
        rm.returnCourt(testCourt1, testTimeEnd);

        assertEquals(1, rm.getAllArchiveReservations().size());
        assertEquals(1, rm.getAllCurrentReservations().size());
        rm.returnCourt(testCourt2);

        assertEquals(2, rm.getAllArchiveReservations().size());
        assertEquals(0, rm.getAllCurrentReservations().size());

        assertThrows(MainException.class, () -> rm.returnCourt(null));
    }

    @Test
    void testCheckingClientBalance() {
        var testSuperTimeEnd = LocalDateTime.of(2023, Month.JUNE, 5, 12, 0);
        var testSuperTimeEnd2 = LocalDateTime.of(2023, Month.JUNE, 6, 12, 0);

        rm.makeReservation(testClient1,testCourt1,testTimeStart);
        rm.makeReservation(testClient1, testCourt2, testTimeStart);
        rm.makeReservation(testClient1, testCourt3, testTimeStart);

        assertEquals(0, rm.checkClientReservationBalance(testClient1));
        rm.returnCourt(testCourt1, testTimeEnd);
        assertEquals(300, rm.checkClientReservationBalance(testClient1));

        rm.returnCourt(testCourt2, testSuperTimeEnd);
        assertEquals(3750, rm.checkClientReservationBalance(testClient1));

        rm.returnCourt(testCourt3, testSuperTimeEnd2);
        assertEquals(10800, rm.checkClientReservationBalance(testClient1));

        assertThrows(MainException.class, () -> rm.checkClientReservationBalance(null));
    }

    @Test
    void testGetAllClientReservations() {

    }
}
