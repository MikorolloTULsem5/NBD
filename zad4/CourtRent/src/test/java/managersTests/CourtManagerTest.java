package managersTests;
import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.courts.CourtCassandraRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtManagerTest {

    static CourtCassandraRepository courtRepository = new CourtCassandraRepository();

    @BeforeAll
    @AfterAll
    static void cleanDatabaseFirstAndLastTime() {
        courtRepository.readAll().forEach(courtRepository::delete);
    }
    @BeforeEach
    void cleanDatabase(){
        cleanDatabaseFirstAndLastTime();
    }

    @Test
    void testCreatingCourtManager() {
        CourtManager courtManager = new CourtManager();
        assertNotNull(courtManager);
        assertEquals(0, courtManager.getAllCourts().size());
    }

    @Test
    void testRegisteringNewCourt() {
        CourtManager cm = new CourtManager();
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        Court newCourt = cm.registerCourt(200, 200, 5);
        assertNotNull(newCourt);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourt(newCourt.getCourtId()));
        assertThrows(CourtException.class, () -> cm.registerCourt(300, 300, 5));
        assertEquals(1, cm.getAllCourts().size());

        cm.registerCourt(200, 200, 6);
        cm.registerCourt(200, 200, 7);
        cm.registerCourt(200, 200, 8);
        assertEquals(4, cm.getAllCourts().size());
    }

    @Test
    void testGettingCourt() {
        CourtManager cm = new CourtManager();
        assertNotNull(cm);

        Court testCourt1 = cm.registerCourt(10,50,1);
        assertNotNull(testCourt1);
        assertEquals(1, cm.getAllCourts().size());

        Court testCourt2 = cm.registerCourt(14,67,2);
        assertNotNull(testCourt2);
        assertEquals(2, cm.getAllCourts().size());

        assertEquals(testCourt1, cm.getCourt(testCourt1.getCourtId()));
        assertEquals(testCourt2, cm.getCourt(testCourt2.getCourtId()));
        assertNull(cm.getCourt(UUID.randomUUID()));
    }

    @Test
    void testUnregisteringCourt() {
        CourtManager cm = new CourtManager();
        assertNotNull(cm);

        Court testCourt1 = cm.registerCourt(10,50,1);
        assertNotNull(testCourt1);
        assertEquals(1, cm.getAllCourts().size());
        Court testCourt2 = cm.registerCourt(14,67,2);
        assertNotNull(testCourt2);
        assertEquals(2, cm.getAllCourts().size());

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt1, cm.getCourt(testCourt1.getCourtId()));
        assertFalse(testCourt1.isArchive());

        cm.unregisterCourt(testCourt1);

        assertEquals(2, cm.getAllCourts().size());
        Court dbCourt = cm.getCourt(testCourt1.getCourtId());
        assertNotNull(dbCourt);
        assertTrue(dbCourt.isArchive());

        // Testujemy wyrejestrowanie boiska ktore nie nalezy do repozytorium
        Court testCourt3 = new Court(41,11,3);
        assertNotNull(testCourt3);
        assertFalse(testCourt3.isArchive());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterCourt(null));
        assertEquals(2, cm.getAllCourts().size());
    }

    @Test
    public void testGetByCourtNumber(){
        CourtManager cm = new CourtManager();
        assertNotNull(cm);

        Court testCourt1 = cm.registerCourt(10,50,1);
        assertNotNull(testCourt1);
        assertEquals(1, cm.getAllCourts().size());
        Court testCourt2 = cm.registerCourt(14,67,2);
        assertNotNull(testCourt2);
        assertEquals(2, cm.getAllCourts().size());

        Court newCourt = cm.getCourtByCourtNumber(1);
        assertNotNull(newCourt);
        assertEquals(testCourt1, newCourt);
        Court newCourt2 = cm.getCourtByCourtNumber(4);
        assertNull(newCourt2);
    }
}
