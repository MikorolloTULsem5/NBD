package repositoryTests;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Normal;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
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

public class ReservationRepositoryTest {
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
        testClient3.setArchive(true);
        assertThrows(ClientException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient3, testCourt3, testTimeStart))));

        //Archive court
        testCourt4.setArchive(true);
        assertThrows(ClientException.class, () -> reservationRepository.create(
                ReservationMapper.toMongoReservation(new Reservation(testClient2, testCourt4, testTimeStart))));
    }

//    @Test
//    void testAddingNewRecordWithLogicBasicToDB() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<Long> query = cb.createQuery(Long.class);
//        From<Reservation, Reservation> from = query.from(Reservation.class);
//        query.select(cb.count(from));
//        long count = reservationRepository.getEntityManager().createQuery(query).getSingleResult();
//
//        assertEquals(0, count);
//        Reservation reservation = reservationRepository.create(testClient1,testCourt1,testTimeStart);
//        assertEquals(testClient1, reservation.getClient());
//        assertEquals(testCourt1, reservation.getCourt());
//        assertEquals(testTimeStart, reservation.getBeginTime());
//        assertTrue(testCourt1.isRented());
//        count = reservationRepository.getEntityManager().createQuery(query).getSingleResult();
//        assertEquals(1, count);
//        assertThrows(JakartaException.class, () -> reservationRepository.create(null));
//    }
//
//    @Test
//    void testAddingNewRecordWithLogicViolationsToDB() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<Long> query = cb.createQuery(Long.class);
//        From<Reservation, Reservation> from = query.from(Reservation.class);
//        query.select(cb.count(from));
//        long count = reservationRepository.getEntityManager().createQuery(query).getSingleResult();
//        assertEquals(0, count);
//
//        testCourt1.setRented(true);
//        courtRepository.update(testCourt1);
//        assertThrows(ReservationException.class, () -> reservationRepository.create(testClient1,testCourt1,testTimeStart));
//        count = reservationRepository.getEntityManager().createQuery(query).getSingleResult();
//        assertEquals(0, count);
//
//        testCourt2.setArchive(true);
//        courtRepository.update(testCourt2);
//        assertThrows(CourtException.class, () -> reservationRepository.create(testClient1,testCourt2,testTimeStart));
//        count = reservationRepository.getEntityManager().createQuery(query).getSingleResult();
//        assertEquals(0, count);
//
//        testClient2.setArchive(true);
//        clientRepository.update(testClient2);
//        assertThrows(ClientException.class, () -> reservationRepository.create(testClient2,testCourt3,testTimeStart));
//        count = reservationRepository.getEntityManager().createQuery(query).getSingleResult();
//        assertEquals(0, count);
//    }
//
//    @Test
//    void testFindingRecordsInDB() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        Reservation reservation1 = reservationRepository.create(testClient1,testCourt1,testTimeStart);
//        Reservation reservation2 = reservationRepository.create(testClient1,testCourt2,testTimeStart);
//        Reservation reservation3 = reservationRepository.create(testClient2,testCourt3,testTimeStart);
//
//        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
//        Root<Reservation> courtRoot = query.from(Reservation.class);
//        query.select(courtRoot).where(cb.equal(courtRoot.get(Reservation_.CLIENT), testClient1));
//
//        List<Reservation> reservations = reservationRepository.find(query);
//        assertEquals(2, reservations.size());
//        assertEquals(reservation1, reservations.get(0));
//        assertEquals(reservation2, reservations.get(1));
//
//        query.select(courtRoot).where(cb.equal(courtRoot.get(Reservation_.CLIENT), testClient3));
//        assertTrue(reservationRepository.find(query).isEmpty());
//
//        List<Reservation> resultAll = reservationRepository.findAll();
//        assertEquals(3, resultAll.size());
//        assertEquals(reservation1, resultAll.get(0));
//        assertEquals(reservation2, resultAll.get(1));
//        assertEquals(reservation3, resultAll.get(2));
//    }
//
//    @Test
//    void testFindingByUUID() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        Reservation reservation1 = reservationRepository.create(testClient1, testCourt1, testTimeStart);
//        Reservation reservation2 = reservationRepository.create(testClient2, testCourt2, testTimeStart);
//
//        assertEquals(reservation1, reservationRepository.findByUUID(reservation1.getId()));
//        assertEquals(reservation2, reservationRepository.findByUUID(reservation2.getId()));
//
//        assertNull(reservationRepository.findByUUID(UUID.randomUUID()));
//        assertThrows(JakartaException.class, () -> reservationRepository.findByUUID(null));
//    }
//
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
//    @Test
//    void testUpdatingRecordsInDB() {
//        ReservationRepository reservationRepository = new ReservationRepository("test");
//        assertNotNull(reservationRepository);
//
//        Reservation reservation1 = reservationRepository.create(testClient1, testCourt1, testTimeStart);
//        Reservation reservation2 = reservationRepository.create(testClient2, testCourt2, testTimeStart);
//        Reservation reservation3 = new Reservation(UUID.randomUUID(),testClient3, testCourt3, testTimeStart);
//
//        assertNull(reservationRepository.findByUUID(reservation1.getId()).getEndTime());
//        reservation1.endReservation(testTimeEnd);
//        reservationRepository.update(reservation1);
//        assertEquals(testTimeEnd, reservationRepository.findByUUID(reservation1.getId()).getEndTime());
//
//        assertThrows(JakartaException.class, () -> reservationRepository.update(reservation3));
//        assertThrows(JakartaException.class, () -> reservationRepository.update(null));
//    }
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
