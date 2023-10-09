package nbd.gV;

import nbd.gV.clientstype.ClientType;
import nbd.gV.clientstype.Normal;

import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        ClientType testClientType = new Normal();
        Client testClient = new Client("John", "Smith", "123456789", testClientType);
        Court testCourt = new FootballCourt(1, 100, 1);
        LocalDateTime now = LocalDateTime.of(2023, Month.JUNE, 3, 22, 15);
        LocalDateTime then = LocalDateTime.of(2023, Month.JUNE, 3, 20, 7);
        Reservation reservation = new Reservation(1, testClient, testCourt, then);
        reservation.endReservation(now);

        System.out.println(reservation.getReservationInfo());
    }
}