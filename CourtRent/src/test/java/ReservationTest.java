import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.courts.FootballCourt;
import nbd.gV.reservations.Reservation;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Normal;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

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
    UUID testUUID;

    @BeforeEach
    void setUp() {
        testClientType = new Normal();
        testClient = new Client("John", "Smith", "123456789", testClientType);
        testCourt = new FootballCourt(1, 100, 1);
        testUUID = UUID.randomUUID();
    }

    @Test
    void testCreatingReservation() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        assertNotNull(now);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, now);
        assertNotNull(reservation);

        assertEquals(testUUID, reservation.getId());
        assertEquals(testClient, reservation.getClient());
        assertEquals(testCourt, reservation.getCourt());
        assertEquals(now, reservation.getBeginTime());
        assertNull(reservation.getEndTime());
    }

    @Test
    void testCreatingReservationWithNullDate() {
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, null);
        assertNotNull(reservation);
        LocalDateTime now = LocalDateTime.now();
        assertNotNull(now);

        assertEquals(testUUID, reservation.getId());
        assertEquals(testClient, reservation.getClient());
        assertEquals(testCourt, reservation.getCourt());
        assertEquals(0, Duration.between(reservation.getBeginTime(), now).getSeconds());
        assertNull(reservation.getEndTime());
    }


    @Test
    void testConstructorException() {
        assertThrows(MainException.class, () -> new Reservation(null, testClient, testCourt, null));
        assertThrows(MainException.class, () -> new Reservation(testUUID, null, testCourt, null));
        assertThrows(MainException.class, () -> new Reservation(testUUID, testClient, null, null));
    }

    @Test
    void testEndingReservation() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);
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
        assertEquals(now, reservation.getEndTime());

        assertTrue(testClient.isArchive());
        assertTrue(testCourt.isArchive());

        assertThrows(ReservationException.class, () ->
                reservation.endReservation(LocalDateTime.of(2023, Month.JUNE, 3, 20, 8)));
    }

    @Test
    void testEndingReservationWithNullDate() {
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);
        assertNotNull(reservation);

        assertNull(reservation.getEndTime());

        assertFalse(testClient.isArchive());
        assertFalse(testCourt.isArchive());

        reservation.endReservation(null);
        LocalDateTime now = LocalDateTime.now();
        assertNotNull(reservation.getEndTime());

        assertNotNull(reservation.getEndTime());
        assertEquals(0, Duration.between(reservation.getEndTime(), now).getSeconds());

        assertTrue(testClient.isArchive());
        assertTrue(testCourt.isArchive());

        assertThrows(ReservationException.class, () ->
                reservation.endReservation(LocalDateTime.of(2023, Month.JUNE, 3, 20, 8)));
    }

    @Test
    void testExceedingReservationPermittedTime() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 23, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 19, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);
        assertNotNull(reservation);

        reservation.endReservation(now);

        assertTrue(reservation.getReservationHours() > testClient.getClientMaxHours());
        assertEquals(900, reservation.getReservationCost());
    }

    @Test
    void testGettingReservationInfo() {
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(testUUID, testClient, testCourt, then);

        assertNotNull(reservation);

        String str = "Rezerwacja nr " + testUUID +
                " przez 'Klient - John Smith o numerze PESEL 123456789' boiska: 'Boisko nr 1 " +
                "przeznaczone do pilki noznej o powierzchni 1,00 i koszcie za rezerwację: 150,00 PLN', " +
                "od godziny [03.06.2023, 20:07].%n".formatted();
        assertEquals(str, reservation.getReservationInfo());

        reservation.endReservation(now);

        str = ("Rezerwacja nr " + testUUID +
                " przez 'Klient - John Smith o numerze PESEL 123456789' boiska: 'Boisko nr 1 " +
                "przeznaczone do pilki noznej o powierzchni 1,00 i koszcie za rezerwację: 150,00 PLN', " +
                "od godziny [03.06.2023, 20:07] do godziny [03.06.2023, 22:15].%n").formatted();
        assertEquals(str, reservation.getReservationInfo());
    }
}