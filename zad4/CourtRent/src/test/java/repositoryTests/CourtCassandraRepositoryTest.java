package repositoryTests;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import nbd.gV.SchemaConst;
import nbd.gV.courts.Court;

import nbd.gV.repositories.clients.ClientCassandraRepository;
import nbd.gV.repositories.courts.CourtCassandraRepository;
import nbd.gV.repositories.courts.CourtMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.UUID;

import static nbd.gV.SchemaConst.CLIENTS_TABLE;
import static nbd.gV.SchemaConst.COURTS_TABLE;
import static nbd.gV.SchemaConst.COURTS_TABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtCassandraRepositoryTest {
    static CourtCassandraRepository courtRepository;

    static CqlSession session;
    
    Court court1;
    Court court2;
    Court court3;

    @BeforeAll
    static void setupTestSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandranode1", 9042))
                .addContactPoint(new InetSocketAddress("cassandranode2", 9043))
                .addContactPoint(new InetSocketAddress("cassandranode3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("admin", "adminpassword")
                .withKeyspace(CqlIdentifier.fromCql(SchemaConst.RESERVE_A_COURT_NAMESPACE))
                .build();

        session.execute("DROP TABLE IF EXISTS " + COURTS_TABLE);

        courtRepository = new CourtCassandraRepository();
    }

//    @AfterAll
    static void cleanTable() {
        session.execute("TRUNCATE " + COURTS_TABLE);
    }

    @BeforeEach
    void initData() {
        cleanTable();

        court1 = new Court(100, 200, 1);
        court2 = new Court(200, 200, 2);
        court3 = new Court(300, 300, 3);
    }


    @Test
    void testCreatingRepository() {
        assertNotNull(courtRepository);
        assertTrue(session.execute("SELECT * FROM " + COURTS_TABLE).all().isEmpty());
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        assertEquals(1, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court2);
        assertEquals(2, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        assertEquals(1, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        assertEquals(1, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
    }
//
//    @Test
//    void testFindingDocumentsInDBPositive() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var courtsList1 = courtRepository.read(Filters.eq("area", 300));
//        assertEquals(1, courtsList1.size());
//        assertEquals(courtMapper3, courtsList1.get(0));
//
//        var clientsList2 = courtRepository.read(Filters.eq("basecost", 200));
//        assertEquals(2, clientsList2.size());
//        assertEquals(courtMapper1, clientsList2.get(0));
//        assertEquals(courtMapper2, clientsList2.get(1));
//    }
//
//    @Test
//    void testFindingDocumentsInDBNegative() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var clientsList = courtRepository.read(Filters.eq("area", 999));
//        assertEquals(0, clientsList.size());
//    }
//
//    @Test
//    void testFindingAllDocumentsInDB() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        var clientsList = courtRepository.readAll();
//        assertEquals(3, clientsList.size());
//        assertEquals(courtMapper1, clientsList.get(0));
//        assertEquals(courtMapper2, clientsList.get(1));
//        assertEquals(courtMapper3, clientsList.get(2));
//    }
//
//    @Test
//    void testFindingByUUID() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        CourtMapper couMapper1 = courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId()));
//        assertNotNull(couMapper1);
//        assertEquals(courtMapper1, couMapper1);
//
//        CourtMapper couMapper3 = courtRepository.readByUUID(UUID.fromString(courtMapper3.getCourtId()));
//        assertNotNull(couMapper3);
//        assertEquals(courtMapper3, couMapper3);
//    }
//
//    @Test
//    void testDeletingDocumentsInDBPositive() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertTrue(courtRepository.delete(UUID.fromString(courtMapper2.getCourtId())));
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//
//        //Check the rest
//        var courtMappersLists = courtRepository.readAll();
//        assertEquals(2, courtMappersLists.size());
//        assertEquals(courtMapper1, courtMappersLists.get(0));
//        assertEquals(courtMapper3, courtMappersLists.get(1));
//    }
//
//    @Test
//    void testDeletingDocumentsInDBNegative() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertTrue(courtRepository.delete(UUID.fromString(courtMapper3.getCourtId())));
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertFalse(courtRepository.delete(UUID.fromString(courtMapper3.getCourtId())));
//        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
//    }
//
//    @Test
//    void testUpdatingRecordsInDBPositive() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertEquals(200,
//                courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId())).getBaseCost());
//        assertTrue(courtRepository.update(UUID.fromString(courtMapper1.getCourtId()),
//                "basecost", 350));
//        assertEquals(350,
//                courtRepository.readByUUID(UUID.fromString(courtMapper1.getCourtId())).getBaseCost());
//
//        //Test adding new value to document
//        assertFalse(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
//                .find(Filters.eq("_id", courtMapper2.getCourtId().toString()))
//                .into(new ArrayList<>()).get(0).containsKey("field"));
//
//        assertTrue(courtRepository.update(UUID.fromString(courtMapper2.getCourtId()),
//                "field", "newValue"));
//
//        assertTrue(courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
//                .find(Filters.eq("_id", courtMapper2.getCourtId().toString()))
//                .into(new ArrayList<>()).get(0).containsKey("field"));
//
//        assertEquals("newValue",
//                courtRepository.getDatabase().getCollection(courtRepository.getCollectionName(), Document.class)
//                        .find(Filters.eq("_id", courtMapper2.getCourtId().toString()))
//                        .into(new ArrayList<>()).get(0).getString("field"));
//    }
//    @Test
//    void testUpdatingRecordsInDBNegative() {
//        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
//        assertTrue(courtRepository.create(courtMapper1));
//        assertTrue(courtRepository.create(courtMapper2));
//        assertTrue(courtRepository.create(courtMapper3));
//        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());
//
//        assertThrows(MyMongoException.class,
//                () -> courtRepository.update(UUID.fromString(courtMapper3.getCourtId()),
//                        "_id", UUID.randomUUID().toString()));
//
//        assertFalse(courtRepository.update(UUID.randomUUID(), "area", 435.0));
//    }
}
