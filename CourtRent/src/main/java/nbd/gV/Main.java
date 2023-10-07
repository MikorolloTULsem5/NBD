package nbd.gV;

import nbd.gV.clientstype.Athlete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Court court = new BasketballCourt(10, 100, 1);
        Client client = new Client("John", "Smith", "643534534", new Athlete());

        System.out.print(court.getCourtInfo());
        System.out.print(client.getClientInfo());

        Reservation reservation = new Reservation(1, client, court, LocalDateTime.now());
        System.out.print(reservation.getReservationInfo());

        List<String> list = new ArrayList<>();
        System.out.println(list.add("aaa"));
        System.out.println(list.add(null));
    }
}