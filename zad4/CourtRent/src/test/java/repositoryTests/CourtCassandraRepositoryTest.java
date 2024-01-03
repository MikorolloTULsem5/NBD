package repositoryTests;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

import nbd.gV.SchemaConst;
import nbd.gV.courts.Court;
import nbd.gV.repositories.courts.CourtCassandraRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import java.util.Comparator;

import static nbd.gV.SchemaConst.COURTS_TABLE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @AfterAll
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

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        assertEquals(court3, courtRepository.read(3));
        assertEquals(court1, courtRepository.read(1));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        assertEquals(1, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        assertNull(courtRepository.read(5));
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        var courtList = courtRepository.readAll();
        courtList.sort(Comparator.comparing(Court::getCourtNumber));
        assertEquals(3, courtList.size());

        assertEquals(court1, courtList.get(0));
        assertEquals(court2, courtList.get(1));
        assertEquals(court3, courtList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        assertEquals(court1, courtRepository.readByUUID(court1.getCourtId()));
        assertEquals(court3, courtRepository.readByUUID(court3.getCourtId()));
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        courtRepository.delete(court2);
        assertEquals(2, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        //Check the rest
        var courtList = courtRepository.readAll();
        courtList.sort(Comparator.comparing(Court::getCourtNumber));
        assertEquals(2, courtList.size());

        assertEquals(court1, courtList.get(0));
        assertEquals(court3, courtList.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        courtRepository.delete(court2);
        assertEquals(2, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        courtRepository.delete(court2);
        assertEquals(2, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
    }

    @Test
    void testUpdatingRecordsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        Court courtToUpd = courtRepository.readByUUID(court1.getCourtId());
        assertEquals(200, courtToUpd.getBaseCost());

        courtToUpd.setBaseCost(305);

        courtRepository.update(courtToUpd);
        assertEquals(305, courtRepository.readByUUID(court1.getCourtId()).getBaseCost());
    }

    @Test
    void testUpdatingRecordsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());
        courtRepository.create(court1);
        courtRepository.create(court2);
        courtRepository.create(court3);
        assertEquals(3, session.execute("SELECT * FROM " + COURTS_TABLE).all().size());

        Court newCourt = new Court(400, 165 ,4);
        courtRepository.update(newCourt);

        var courtList = courtRepository.readAll();
        courtList.sort(Comparator.comparing(Court::getCourtNumber));
        assertEquals(4, courtList.size());

        assertEquals(newCourt, courtList.get(3));
    }
}
