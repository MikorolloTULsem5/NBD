package repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Normal;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.repositories.ClientMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.ReservationMongoRepository;
import nbd.gV.reservations.Reservation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationMongoRepositoryTest {
    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
    static final ClientMongoRepository clientRepository = new ClientMongoRepository();
    ClientType testClientType;

    Client testClient1;
    Client testClient2;
    Client testClient3;
    Court testCourt1;
    Court testCourt2;
    Court testCourt3;
    Court testCourt4;
    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    private MongoCollection<ReservationMapper> getTestCollection() {
        return reservationRepository.getDatabase()
                .getCollection(reservationRepository.getCollectionName(), ReservationMapper.class);
    }

    @BeforeAll
    static void cleanDB() {
        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
                ReservationMapper.class).deleteMany(Filters.empty());
        clientRepository.readAll().forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getClientID())));
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getCourtId())));
    }

    @BeforeEach
    void setUp() {
        cleanDB();
        testClientType = new Normal();

        testClient1 = new Client("John", "Smith", "12345678901", testClientType);
        testClient2 = new Client("Eva", "Brown", "12345678902", testClientType);
        testClient3 = new Client("Adam", "Long", "12345678903", testClientType);
        clientRepository.create(ClientMapper.toMongoClient(testClient1));
        clientRepository.create(ClientMapper.toMongoClient(testClient2));
        clientRepository.create(ClientMapper.toMongoClient(testClient3));

        testCourt1 = new Court(1000, 100, 1);
        testCourt2 = new Court(1000, 100, 2);
        testCourt3 = new Court(1000, 100, 3);
        testCourt4 = new Court(1000, 100, 4);

        courtRepository.create(CourtMapper.toMongoCourt(testCourt1));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt2));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt3));
        courtRepository.create(CourtMapper.toMongoCourt(testCourt4));

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingRepository() {
        ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
        assertNotNull(reservationRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        Reservation reservation2 = new Reservation(testClient2, testCourt2, testTimeStart);
        assertNotNull(reservation2);
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        //Reserve reserved court
        Reservation reservation2 = new Reservation(testClient2, testCourt1, testTimeStart);
        assertNotNull(reservation2);
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(reservation2)));

        //No client in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(new Client("John", "Blade",
                        "12345678911", new Normal()), testCourt3, testTimeStart))));

        //No court in the database
        assertThrows(ReservationException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient3, new Court(1000, 100,
                        5), testTimeStart))));

        //Archive client
        clientRepository.update(testClient3.getClientID(), "archive", true);
        assertThrows(ClientException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient3, testCourt3, testTimeStart))));

        //Archive court
        courtRepository.update(testCourt4.getCourtId(), "archive", true);
        assertThrows(CourtException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt4, testTimeStart))));
    }

    @Test
    void testFindingRecordsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        ReservationMapper reservationMapper1 = ReservationMapper.toMongoReservation(new Reservation(testClient1,
                        testCourt1, LocalDateTime.now()));
        assertTrue(reservationRepository.create(reservationMapper1));
        ReservationMapper reservationMapper2 = ReservationMapper.toMongoReservation(new Reservation(testClient2,
                testCourt2, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper2));
        ReservationMapper reservationMapper3 = ReservationMapper.toMongoReservation( new Reservation(testClient3,
                testCourt3, testTimeStart));
        assertTrue(reservationRepository.create(reservationMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var reservationsList1 = reservationRepository.read(Filters.eq("clientid",
                testClient1.getClientID().toString()));
        assertEquals(1, reservationsList1.size());
        assertEquals(reservationMapper1, reservationsList1.get(0));

        var reservationsList2 =  reservationRepository.read(Filters.eq("begintime",
                testTimeStart.toString()));
        assertEquals(reservationMapper2, reservationsList2.get(0));
        assertEquals(reservationMapper3, reservationsList2.get(1));
    }

    @Test
    void testFindingRecordsInDBNegative() {

    }

    @Test
    void testFindingByUUIDPositive() {

    }

    @Test
    void testFindingByUUIDNegative() {

//        assertNull(reservationRepository.findByUUID(UUID.randomUUID()));
//        assertThrows(JakartaException.class, () -> reservationRepository.findByUUID(null));
    }

    //    @Test
//    void testDeletingRecordsInDB() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        Reservation reservation1 = reservationRepository.create(testClient1, testCourt1, testTimeStart);
//        Reservation reservation2 = reservationRepository.create(testClient2, testCourt2, testTimeStart);
//
//        assertEquals(2, reservationRepository.findAll().size());
//        reservationRepository.delete(reservation2);
//        assertEquals(1, reservationRepository.findAll().size());
//
//        reservationRepository.delete(reservation1);
//        var reservations = reservationRepository.findAll();
//        assertTrue(reservations.isEmpty());
//
//        assertThrows(JakartaException.class, () -> reservationRepository.delete(null));
//        assertThrows(JakartaException.class, () -> reservationRepository.delete(reservation1));
//        assertEquals(0, reservationRepository.findAll().size());
//    }
//
    @Test
    void testUpdatingRecordsInDBPositive() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.update(testCourt1, testTimeEnd);
        //readyyyy
    }

    @Test
    void testUpdatingRecordsInDBNegative() {
        Reservation reservation = new Reservation(testClient1, testCourt1, testTimeStart);
        assertNotNull(reservation);
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        reservationRepository.create(ReservationMapper.toMongoReservation(reservation));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        reservationRepository.update(testCourt1, testTimeEnd);
        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt1, testTimeEnd));


//        assertThrows(JakartaException.class, () -> reservationRepository.update(reservation3));
//        assertThrows(JakartaException.class, () -> reservationRepository.update(null));
    }
//
//    @Test
//    void testUpdatingRecordsInWithLogicDB() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        Reservation reservation1 = reservationRepository.create(testClient1, testCourt1, testTimeStart);
//        Reservation reservation2 = reservationRepository.create(testClient2, testCourt2, testTimeStart);
//
//        assertNull(reservationRepository.findByUUID(reservation1.getId()).getEndTime());
//        reservationRepository.update(testCourt1, testTimeEnd);
//        assertEquals(testTimeEnd, reservationRepository.findByUUID(reservation1.getId()).getEndTime());
//        assertFalse(testCourt1.isRented());
//
//        assertThrows(ReservationException.class, () -> reservationRepository.update(testCourt3, testTimeEnd));
//        assertThrows(JakartaException.class, () -> reservationRepository.update(null));
//    }
}
