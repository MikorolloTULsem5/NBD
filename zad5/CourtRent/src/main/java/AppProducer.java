import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Normal;

import nbd.gV.courts.Court;

import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;

import nbd.gV.repositories.ClientMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationManager;

import java.time.LocalDateTime;
import java.time.Month;

public class AppProducer {
    public static void main(String[] args) {
        ClientMongoRepository clientRepository = new ClientMongoRepository();
        CourtMongoRepository courtRepository = new CourtMongoRepository();
        ReservationManager rm = new ReservationManager();


        // Data
        ClientType testClientType = new Normal();

        Client testClient1 = new Client("John", "Smith", "12345678901", testClientType);
        Client testClient2 = new Client("Eva", "Brown", "12345678902", testClientType);
        Client testClient3 = new Client("Adam", "Long", "12345678903", testClientType);
        clientRepository.create(ClientMapper.toMongoClient(testClient1));
        clientRepository.create(ClientMapper.toMongoClient(testClient2));
        clientRepository.create(ClientMapper.toMongoClient(testClient3));

        Court testCourt1 = new Court(1000, 100, 1);
        Court testCourt2 = new Court(1000, 100, 2);
        Court testCourt3 = new Court(1000, 100, 3);
        Court testCourt4 = new Court(1000, 100, 4);

        courtRepository.create(CourtMapper.toMongoCourt(testCourt1));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt2));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt3));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt4));

        LocalDateTime testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        LocalDateTime testTimeSecond = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);


        //Creating reservations
        Reservation newReservation = rm.makeReservation(testClient1, testCourt1, testTimeStart);
        Reservation newReservation2 = rm.makeReservation(testClient2, testCourt2, testTimeSecond);
        Reservation newReservation3 = rm.makeReservation(testClient3, testCourt3, testTimeStart);
    }
}
