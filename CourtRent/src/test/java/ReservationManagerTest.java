import nbd.gV.Repository;
import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Coach;
import nbd.gV.clients.Normal;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationManagerTest {

    Client testClient1;
    Client testClient2;
    Client testClient3;
    ClientType testClientType;
    Court testCourt1;
    Court testCourt2;
    Court testCourt3;
    Court testCourt4;
    Court testCourt5;
    Reservation testReservation1;
    Reservation testReservation2;
    Reservation testReservation3;

    Repository<Reservation> testCurrentReservation;
    Repository<Reservation> testArchiveReservation;

    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    UUID testUUID1;
    UUID testUUID2;
    UUID testUUID3;
    @BeforeEach
    void setUp() {
        testClientType = new Normal();
        testClient1 = new Client("John", "Smith", "123456789", testClientType);
        testClient2 = new Client("Eva", "Brown", "41565646", testClientType);
        testClient3 = new Client("Adam", "Long", "81657664", testClientType);

        testCourt1 = new Court(1000, 100, 1);
        testCourt2 = new Court(1000, 100, 2);
        testCourt3 = new Court(1000, 100, 3);
        testCourt4 = new Court(1000, 100, 4);
        testCourt5 = new Court(1000, 100, 5);


        testCurrentReservation = new Repository<>();
        testArchiveReservation = new Repository<>();

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingReservationManager() {
        testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        testReservation2 = new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart);
        testReservation3 = new Reservation(UUID.randomUUID(), testClient3, testCourt3, testTimeStart);

        testCurrentReservation.add(testReservation1);
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);
        assertNotNull(rm);

        assertEquals(rm.getAllCurrentReservations().size(), 1);
        assertEquals(rm.getAllArchiveReservations().size(), 0);

        testCurrentReservation.add(testReservation2);
        testArchiveReservation.add(testReservation3);

        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertEquals(rm.getAllArchiveReservations().size(), 1);
    }

    @Test
    void testMakingReservation() {
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);
        assertNotNull(rm);
        assertEquals(rm.getAllCurrentReservations().size(), 0);
        assertFalse(testCourt1.isRented());

        Reservation newReservation = rm.makeReservation(testClient1, testCourt1, testTimeStart);

        assertEquals(rm.getAllCurrentReservations().size(), 1);
        assertEquals(newReservation, testCurrentReservation.get(0));
        assertTrue(testCourt1.isRented());

        assertFalse(testCourt2.isRented());
        Reservation newReservation2 = rm.makeReservation(testClient1, testCourt2);

        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertEquals(newReservation2, testCurrentReservation.get(1));
        assertTrue(testCourt2.isRented());

        assertThrows(ReservationException.class, () -> rm.makeReservation(testClient1, testCourt1, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertTrue(testCourt1.isRented());

        testClient2.setArchive(true);
        assertFalse(testCourt3.isRented());
        assertThrows(ClientException.class, () -> rm.makeReservation(testClient2, testCourt3, testTimeStart));
        assertEquals(rm.getAllCurrentReservations().size(), 2);
        assertFalse(testCourt3.isRented());

        testCourt4.setArchive(true);
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
        ReservationManager rm = new ReservationManager();
        assertNotNull(rm);

        assertEquals(0, rm.getAllCurrentReservations().size());
        Reservation newReservation = rm.makeReservation(testClient1, testCourt1);
        var reservations = rm.getAllCurrentReservations();
        assertEquals(1, reservations.size());
        assertEquals(newReservation, reservations.get(0));
    }

    @Test
    void testDeletingReservation() {
        testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        testReservation2 = new Reservation(UUID.randomUUID(), testClient2, testCourt2, testTimeStart);
        testCurrentReservation.add(testReservation1);
        testCurrentReservation.add(testReservation2);
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);
        assertNotNull(rm);

        assertEquals(0, testArchiveReservation.size());
        assertEquals(2, testCurrentReservation.size());
        rm.returnCourt(testCourt1, testTimeEnd);

        assertEquals(1, testArchiveReservation.size());
        assertEquals(1, testCurrentReservation.size());
        rm.returnCourt(testCourt2);

        assertEquals(2, testArchiveReservation.size());
        assertEquals(0, testCurrentReservation.size());

        assertThrows(MainException.class, () -> rm.returnCourt(null));
        assertThrows(CourtException.class, () -> rm.returnCourt(testCourt3));
    }

    @Test
    void testGettingClientReservations() {
        testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        testReservation2 = new Reservation(UUID.randomUUID(), testClient1, testCourt2, testTimeStart);
        testReservation3 = new Reservation(UUID.randomUUID(), testClient3, testCourt3, testTimeStart);
        testCurrentReservation.add(testReservation1);
        testCurrentReservation.add(testReservation2);
        testCurrentReservation.add(testReservation3);
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);

        var reservations1 = rm.getAllClientReservations(testClient1);
        assertEquals(2, reservations1.size());
        assertTrue(Arrays.deepEquals(new Reservation[]{testReservation1, testReservation2}, reservations1.toArray()));

        var reservations2 = rm.getAllClientReservations(testClient2);
        assertEquals(0, reservations2.size());
        assertNotNull(reservations2);

        var reservations3 = rm.getAllClientReservations(testClient3);
        assertEquals(1, reservations3.size());
        assertTrue(Arrays.deepEquals(new Reservation[]{testReservation3}, reservations3.toArray()));

        assertThrows(MainException.class, () -> rm.getAllClientReservations(null));
    }

    @Test
    void testGettingCourtReservations() {
        testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        testReservation2 = new Reservation(UUID.randomUUID(), testClient1, testCourt2, testTimeStart);
        testCurrentReservation.add(testReservation1);
        testCurrentReservation.add(testReservation2);
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);
        assertNotNull(rm);

        assertEquals(testReservation1, rm.getCourtReservation(testCourt1));
        assertEquals(testReservation2, rm.getCourtReservation(testCourt2));

        assertThrows(CourtException.class, () -> rm.getCourtReservation(testCourt3));
        assertThrows(MainException.class, () -> rm.getCourtReservation(null));
    }

    @Test
    void testCheckingClientBalanceAndChangingType() {
        var testSuperTimeEnd = LocalDateTime.of(2023, Month.JUNE, 5, 12, 0);
        var testSuperTimeEnd2 = LocalDateTime.of(2023, Month.JUNE, 6, 12, 0);
        testReservation1 = new Reservation(UUID.randomUUID(), testClient1, testCourt1, testTimeStart);
        testReservation2 = new Reservation(UUID.randomUUID(), testClient1, testCourt2, testTimeStart);
        testReservation3 = new Reservation(UUID.randomUUID(), testClient1, testCourt3, testTimeStart);
        testCurrentReservation.add(testReservation1);
        testCurrentReservation.add(testReservation2);
        testCurrentReservation.add(testReservation3);
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);
        assertNotNull(rm);

        assertEquals(0, rm.checkClientReservationBalance(testClient1));
        rm.returnCourt(testCourt1, testTimeEnd);
        assertEquals(300, rm.checkClientReservationBalance(testClient1));

        assertTrue(testClient1.getClientType() instanceof Normal);
        rm.changeClientType(testClient1);
        assertTrue(testClient1.getClientType() instanceof Normal);


        rm.returnCourt(testCourt2, testSuperTimeEnd);
        assertEquals(3750, rm.checkClientReservationBalance(testClient1));

        rm.changeClientType(testClient1);
        assertTrue(testClient1.getClientType() instanceof Athlete);

        rm.returnCourt(testCourt3, testSuperTimeEnd2);
        assertEquals(10640, rm.checkClientReservationBalance(testClient1));

        rm.changeClientType(testClient1);
        assertTrue(testClient1.getClientType() instanceof Coach);

        assertThrows(MainException.class, () -> rm.checkClientReservationBalance(null));
        assertThrows(MainException.class, () -> rm.changeClientType(null));
    }

    @Test
    void testFindingReservations() {
        testUUID1 = UUID.randomUUID();
        testUUID2 = UUID.randomUUID();
        testUUID3 = UUID.randomUUID();
        testReservation1 = new Reservation(testUUID1, testClient1, testCourt1, testTimeStart);
        testReservation2 = new Reservation(testUUID2, testClient1, testCourt2, testTimeStart);
        testReservation3 = new Reservation(testUUID3, testClient1, testCourt3, testTimeStart);
        testCurrentReservation.add(testReservation1);
        testCurrentReservation.add(testReservation2);
        testCurrentReservation.add(testReservation3);
        ReservationManager rm = new ReservationManager(testCurrentReservation, testArchiveReservation);
        assertNotNull(rm);
        rm.returnCourt(testCourt3);

        Predicate<Reservation> p1 = (r) -> r.getId().equals(testUUID1);
        assertEquals(testReservation1, rm.findReservations(p1).get(0));
        Predicate<Reservation> p2 = (r) -> r.getId().equals(testUUID2);
        assertEquals(testReservation2, rm.findReservations(p2).get(0));
        Predicate<Reservation> p3 = (r) -> r.getId().equals(testUUID3);
        assertEquals(testReservation3, rm.findReservations(p3, true).get(0));
    }
}
