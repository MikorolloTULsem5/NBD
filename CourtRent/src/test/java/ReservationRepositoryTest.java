import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Root;
import nbd.gV.courts.Court;
import nbd.gV.courts.Court_;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.repositories.CourtRepository;
import nbd.gV.repositories.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationRepositoryTest {
    private final Repository<Court> courtRepository = new CourtRepository("test");

    @AfterEach
    void cleanDataBase() {
        List<Court> listOfCourts = courtRepository.findAll();
        listOfCourts.forEach(courtRepository::delete);
    }

    @Test
    void testCreatingRepository() {
        Repository<Court> courtRepository = new CourtRepository("test");
        assertNotNull(courtRepository);
        EntityManager em = courtRepository.getEntityManager();
        assertNotNull(em);
        assertTrue(em.isOpen());
    }

    @Test
    void testAddingNewRecordToDB() {
//        Repository<Court> courtRepository = new CourtRepository("test");
//        assertNotNull(courtRepository);

        CriteriaBuilder cb = courtRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        From<Court, Court> from = query.from(Court.class);
        query.select(cb.count(from));
        long count = courtRepository.getEntityManager().createQuery(query).getSingleResult();

        assertEquals(0, count);
        Court court = new Court(100, 100, 1);
        courtRepository.create(court);
        count = courtRepository.getEntityManager().createQuery(query).getSingleResult();
        assertEquals(1, count);
        assertThrows(JakartaException.class, () -> courtRepository.create(null));
    }

    @Test
    void testFindingRecordsInDB() {
//        Repository<Court> courtRepository = new CourtRepository("test");
//        assertNotNull(courtRepository);

        Court court1 = new Court(100, 400, 1);
        courtRepository.create(court1);
        Court court2 = new Court(200, 100, 2);
        courtRepository.create(court2);
        Court court3 = new Court(200, 100, 3);
        courtRepository.create(court3);
        Court court4 = new Court(100, 400, 4);
        courtRepository.create(court4);

        //Tworzenie zapytania o boiska o powierzchnii 200.0
        CriteriaBuilder cb = courtRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Court> query = cb.createQuery(Court.class);
        Root<Court> courtRoot = query.from(Court.class);
        query.select(courtRoot).where(cb.equal(courtRoot.get(Court_.AREA), 200.0));

        List<Court> resultArea = courtRepository.find(query);
        assertEquals(2, resultArea.size());
        assertEquals(court2, resultArea.get(0));
        assertEquals(court3, resultArea.get(1));

        //Tworzenie zapytania o boiska o koszcie bazowym 400.0
        query.select(courtRoot).where(cb.equal(courtRoot.get(Court_.baseCost), 400.0));

        List<Court> resultBaseCost = courtRepository.find(query);
        assertEquals(2, resultBaseCost.size());
        assertEquals(court1, resultBaseCost.get(0));
        assertEquals(court4, resultBaseCost.get(1));

        //Tworzenie zapytania o boiska o koszcie bazowym 777.0 - takie boisko nie istnieje
        query.select(courtRoot).where(cb.equal(courtRoot.get(Court_.baseCost), 777.0));
        assertTrue(courtRepository.find(query).isEmpty());

        //Pobranie wszystkich boisk z bazy
        List<Court> resultAll = courtRepository.findAll();
        assertEquals(4, resultAll.size());
        assertEquals(court1, resultAll.get(0));
        assertEquals(court2, resultAll.get(1));
        assertEquals(court3, resultAll.get(2));
        assertEquals(court4, resultAll.get(3));
    }

    @Test
    void testFindingByUUID() {
//        Repository<Court> courtRepository = new CourtRepository("test");
//        assertNotNull(courtRepository);

        Court court1 = new Court(100, 400, 1);
        courtRepository.create(court1);
        Court court2 = new Court(200, 100, 2);
        courtRepository.create(court2);
        Court court3 = new Court(200, 100, 3);

        assertEquals(court1, courtRepository.findByUUID(court1.getCourtId()));
        assertEquals(court2, courtRepository.findByUUID(court2.getCourtId()));

        assertNull(courtRepository.findByUUID(court3.getCourtId()));
        assertThrows(JakartaException.class, () -> courtRepository.findByUUID(null));
    }

    @Test
    void testDeletingRecordsInDB() {
//        Repository<Court> courtRepository = new CourtRepository("test");
//        assertNotNull(courtRepository);

        Court court1 = new Court(100, 400, 1);
        courtRepository.create(court1);
        Court court2 = new Court(200, 100, 2);
        courtRepository.create(court2);
        Court court3 = new Court(200, 100, 3);
        courtRepository.create(court3);
        Court court4 = new Court(100, 400, 4);
        courtRepository.create(court4);

        assertEquals(4, courtRepository.findAll().size());
        courtRepository.delete(court2);
        assertEquals(3, courtRepository.findAll().size());

        courtRepository.delete(court4);
        var courts = courtRepository.findAll();
        assertEquals(2, courts.size());
        assertEquals(court1, courts.get(0));
        assertEquals(court3, courts.get(1));

        assertThrows(JakartaException.class, () -> courtRepository.delete(null));
        assertThrows(JakartaException.class, () -> courtRepository.delete(court4));
        assertEquals(2, courtRepository.findAll().size());
    }

    @Test
    void testUpdatingRecordsInDB() {
//        Repository<Court> courtRepository = new CourtRepository("test");
//        assertNotNull(courtRepository);

        Court court1 = new Court(100, 400, 1);
        courtRepository.create(court1);
        Court court2 = new Court(200, 100, 2);
        courtRepository.create(court2);
        Court court3 = new Court(200, 100, 3);
        assertEquals(2, courtRepository.findAll().size());

        assertEquals(100, courtRepository.findByUUID(court2.getCourtId()).getBaseCost());
        court2.setBaseCost(9999);
        courtRepository.update(court2);
        assertEquals(9999, courtRepository.findByUUID(court2.getCourtId()).getBaseCost());

        assertThrows(JakartaException.class, () -> courtRepository.update(court3));
        assertThrows(JakartaException.class, () -> courtRepository.update(null));
    }
}
