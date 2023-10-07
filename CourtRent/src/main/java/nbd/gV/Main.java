package nbd.gV;

import nbd.gV.clientstype.Athlete;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try {
            Court court = new BasketballCourt(10, 100, 1);
            Client client = new Client("John", "Smith", "643534534", new Athlete());

            System.out.print(court.getCourtInfo());
            System.out.print(client.getClientInfo());

            Reservation reservation = new Reservation(1, client, court, LocalDateTime.now());
            System.out.print(reservation.getReservationInfo());


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}