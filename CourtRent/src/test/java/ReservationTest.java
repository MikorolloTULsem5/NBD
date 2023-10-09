import nbd.gV.Client;
import nbd.gV.Court;
import nbd.gV.FootballCourt;
import nbd.gV.Reservation;
import nbd.gV.clientstype.ClientType;
import nbd.gV.clientstype.Normal;
import nbd.gV.exceptions.MainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationTest {
    Client testClient;
    ClientType testClientType;
    Court testCourt;

    @BeforeEach
    void setUp() {
        testClientType = new Normal();
        testClient = new Client("John", "Smith", "123456789", testClientType);
        testCourt = new FootballCourt(1, 100, 1);
    }

    @Test
    void testCreatingReservation() {
        Reservation reservation = new Reservation(1, testClient, testCourt, null);
        assertNotNull(reservation);
        LocalDateTime now = LocalDateTime.now();
        assertNotNull(now);

        assertEquals(1, reservation.getId());
        assertEquals(testClient, reservation.getClient());
        assertEquals(testCourt, reservation.getCourt());
        assertEquals(0, Duration.between(reservation.getBeginTime(), now).toSeconds());
        assertNull(reservation.getEndTime());
    }

    @Test
    void testConstructorException() {
        assertThrows(MainException.class, () -> new Reservation(-5, testClient, testCourt, null));
        assertThrows(MainException.class, () -> new Reservation(1, null, testCourt, null));
        assertThrows(MainException.class, () -> new Reservation(1, testClient, null, null));
    }

    @Test
    void testEndingReservation() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(1, testClient, testCourt, then);
        assertNotNull(reservation);

        assertEquals(0, reservation.getReservationHours());
        assertEquals(0, reservation.getReservationCost());
        assertNull(reservation.getEndTime());

        assertFalse(testClient.isArchive());
        assertFalse(testCourt.isArchive());

        reservation.endReservation(now);

        assertEquals(3, reservation.getReservationHours());
        assertEquals(450, reservation.getReservationCost());
        assertNotNull(reservation.getEndTime());
        assertEquals(0, Duration.between(reservation.getEndTime(), now).toSeconds());

        assertTrue(testClient.isArchive());
        assertTrue(testCourt.isArchive());
    }

    @Test
    void testExceedingReservationPermittedTime() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 23, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 19, 7);
        Reservation reservation = new Reservation(1, testClient, testCourt, then);
        assertNotNull(reservation);

        reservation.endReservation(now);

        assertTrue(reservation.getReservationHours() > testClient.getClientMaxHours());
        assertEquals(900, reservation.getReservationCost());
    }
}
