import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;
import nbd.gV.old.OldRepository;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtManagerTest {
    Court testCourt1;
    Court testCourt2;
    Court testCourt3;

    OldRepository<Court> courtOldRepository;

    @BeforeEach
    void setUp() {
        testCourt1 = new Court(100, 100, 1);
        testCourt2 = new Court(120, 100, 2);
        testCourt3 = new Court(120, 100, 3);
        courtOldRepository = new OldRepository<>();
    }

    @Test
    void testCreatingCourtManager() {
        CourtManager cm = new CourtManager(courtOldRepository);
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        courtOldRepository.add(testCourt1);
        assertEquals(1, cm.getAllCourts().size());

        courtOldRepository.add(testCourt2);
        assertEquals(2, cm.getAllCourts().size());
    }

    @Test
    void testGettingClient() {
        courtOldRepository.add(testCourt1);
        courtOldRepository.add(testCourt2);
        CourtManager cm = new CourtManager(courtOldRepository);
        assertNotNull(cm);

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt1, cm.getCourt(1));
        assertEquals(testCourt2, cm.getCourt(2));
        assertNull(cm.getCourt(8));
        assertThrows(MainException.class, () -> cm.getCourt(0));
    }

    @Test
    void testRegisteringNewCourt() {
        CourtManager cm = new CourtManager(courtOldRepository);
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        Court newCourt = cm.registerCourt(200, 200, 5);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourt(5));
        assertThrows(CourtException.class,
                () -> cm.registerCourt(300, 300, 5));
        assertEquals(1, cm.getAllCourts().size());

        cm.registerCourt(200, 200, 6);
        cm.registerCourt(200, 200, 7);
        cm.registerCourt(200, 200, 8);
        assertEquals(4, cm.getAllCourts().size());
    }

    @Test
    void testCreatingClientManagerWithNullDate() {
        CourtManager cm = new CourtManager();
        assertNotNull(cm);

        Court newCourt = cm.registerCourt(100, 100, 1);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourt(1));
    }

    @Test
    void testUnregisteringClient() {
        courtOldRepository.add(testCourt1);
        courtOldRepository.add(testCourt2);
        CourtManager cm = new CourtManager(courtOldRepository);
        assertNotNull(cm);

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt1, cm.getCourt(1));
        assertFalse(testCourt1.isArchive());

        cm.unregisterCourt(testCourt1);

        assertEquals(1, cm.getAllCourts().size());
        assertNull(cm.getCourt(1));
        assertTrue(testCourt1.isArchive());

        // Testujemy wyrejestrowanie klienta który nie nalezy do repozytorium
        assertNull(cm.getCourt(3));
        assertFalse(testCourt3.isArchive());

        assertThrows(CourtException.class, () -> cm.unregisterCourt(testCourt3));
        assertFalse(testCourt3.isArchive());
        assertEquals(1, cm.getAllCourts().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterCourt(null));
        assertEquals(1, cm.getAllCourts().size());
    }

    @Test
    void testFindingClients() {
        courtOldRepository.add(testCourt1);
        courtOldRepository.add(testCourt2);
        courtOldRepository.add(testCourt3);
        CourtManager cm = new CourtManager(courtOldRepository);
        assertNotNull(cm);

        List<Court> courts1 = cm.findCourts((c) -> c.getArea() == 120);
        assertEquals(2, courts1.size());
        assertEquals(testCourt2, courts1.get(0));
        assertEquals(testCourt3, courts1.get(1));

        List<Court> courts2 = cm.findCourts((c) -> c.getBaseCost() == 100);
        assertEquals(3, courts2.size());
        assertEquals(testCourt1, courts2.get(0));
        assertEquals(testCourt2, courts2.get(1));
        assertEquals(testCourt3, courts2.get(2));

        assertEquals(0, cm.findCourts((c) -> c.getBaseCost() == 200).size());
    }
}
