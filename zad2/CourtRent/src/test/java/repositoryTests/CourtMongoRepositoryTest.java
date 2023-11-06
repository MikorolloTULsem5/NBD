package repositoryTests;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Root;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.old.Repository;
import nbd.gV.repositories.CourtMongoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtMongoRepositoryTest {
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();

    CourtMapper courtMapper1;
    CourtMapper courtMapper2;
    CourtMapper courtMapper3;
    Court court1;
    Court court2;
    Court court3;

    private MongoCollection<CourtMapper> getTestCollection() {
        return courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), CourtMapper.class);
    }

    @BeforeAll
    @AfterAll
    static void cleanFirstAndLastTimeDB() {
        courtRepository.getDatabase()
                .getCollection(courtRepository.getCollectionName(), ClientMapper.class).deleteMany(Filters.empty());
    }

    @BeforeEach
    void initData() {
        getTestCollection().deleteMany(Filters.empty());
        court1 = new Court(100, 200, 1);
        courtMapper1 = CourtMapper.toMongoCourt(court1);

        court2 = new Court(200, 200, 2);
        courtMapper2 = CourtMapper.toMongoCourt(court2);

        court3 = new Court(300, 300, 3);
        courtMapper3 = CourtMapper.toMongoCourt(court3);
    }


    @Test
    void testCreatingRepository() {
        CourtMongoRepository courtRepository = new CourtMongoRepository();
        assertNotNull(courtRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(MyMongoException.class, () -> courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertTrue(courtRepository.create(courtMapper2));
        assertTrue(courtRepository.create(courtMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var courtsList1 = courtRepository.read(Filters.eq("area", 300));
        assertEquals(1, courtsList1.size());
        assertEquals(courtMapper3, courtsList1.get(0));

        var clientsList2 = courtRepository.read(Filters.eq("baseCost", 200));
        assertEquals(2, clientsList2.size());
        assertEquals(courtMapper1, clientsList2.get(0));
        assertEquals(courtMapper2, clientsList2.get(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(courtRepository.create(courtMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList = courtRepository.read(Filters.eq("area", 999));
        assertEquals(0, clientsList.size());
    }
}
