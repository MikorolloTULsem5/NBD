import nbd.gV.courts.BasketballCourt;
import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;
import nbd.gV.courts.FootballCourt;
import nbd.gV.Repository;
import nbd.gV.courts.TennisCourt;
import nbd.gV.courts.VolleyballCourt;
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
    Court testCourt_F;
    Court testCourt_B;
    Court testCourt_V;
    Court testCourt_T;

    Repository<Court> courtRepository;

    @BeforeEach
    void setUp() {
        testCourt_F = new FootballCourt(100, 100, 1);
        testCourt_B = new BasketballCourt(120, 100, 2);
        testCourt_V = new VolleyballCourt(130, 100, 3);
        testCourt_T = new TennisCourt(120, 100, 4);
        courtRepository = new Repository<>();
    }

    @Test
    void testCreatingCourtManager() {
        CourtManager cm = new CourtManager(courtRepository);
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        courtRepository.add(testCourt_F);
        assertEquals(1, cm.getAllCourts().size());

        courtRepository.add(testCourt_B);
        assertEquals(2, cm.getAllCourts().size());

        courtRepository.add(testCourt_V);
        assertEquals(3, cm.getAllCourts().size());

        courtRepository.add(testCourt_T);
        assertEquals(4, cm.getAllCourts().size());
    }

    @Test
    void testGettingClient() {
        courtRepository.add(testCourt_F);
        courtRepository.add(testCourt_T);
        CourtManager cm = new CourtManager(courtRepository);
        assertNotNull(cm);

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt_F, cm.getCourt(1));
        assertEquals(testCourt_T, cm.getCourt(4));
        assertNull(cm.getCourt(8));
        assertThrows(MainException.class, () -> cm.getCourt(0));
    }

    @Test
    void testRegisteringNewClient() {
        CourtManager cm = new CourtManager(courtRepository);
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        Court newCourt = cm.registerCourt(200, 200, 5, CourtManager.CourtType.F);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourt(5));
        assertThrows(CourtException.class,
                () -> cm.registerCourt(300, 300, 5, CourtManager.CourtType.T));
        assertEquals(1, cm.getAllCourts().size());
    }

    @Test
    void testCreatingClientManagerWithNullDate() {
        CourtManager cm = new CourtManager();
        assertNotNull(cm);

        Court newCourt = cm.registerCourt(100, 100, 1, CourtManager.CourtType.B);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourt(1));
    }

    @Test
    void testUnregisteringClient() {
        courtRepository.add(testCourt_F);
        courtRepository.add(testCourt_B);
        CourtManager cm = new CourtManager(courtRepository);
        assertNotNull(cm);

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt_F, cm.getCourt(1));
        assertFalse(testCourt_F.isArchive());

        cm.unregisterCourt(testCourt_F);

        assertEquals(1, cm.getAllCourts().size());
        assertNull(cm.getCourt(1));
        assertTrue(testCourt_F.isArchive());

        // Testujemy wyrejestrowanie klienta który nie nalezy do repozytorium
        assertNull(cm.getCourt(3));
        assertFalse(testCourt_V.isArchive());

        assertThrows(CourtException.class, () -> cm.unregisterCourt(testCourt_V));
        assertFalse(testCourt_V.isArchive());
        assertEquals(1, cm.getAllCourts().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterCourt(null));
        assertEquals(1, cm.getAllCourts().size());
    }

    @Test
    void testFindingClients() {
        courtRepository.add(testCourt_F);
        courtRepository.add(testCourt_B);
        courtRepository.add(testCourt_T);
        CourtManager cm = new CourtManager(courtRepository);
        assertNotNull(cm);

        List<Court> courts1 = cm.findCourts((c) -> c.getArea() == 120);
        assertEquals(2, courts1.size());
        assertEquals(testCourt_B, courts1.get(0));
        assertEquals(testCourt_T, courts1.get(1));

        List<Court> courts2 = cm.findCourts((c) -> c.getBaseCost() == 100);
        assertEquals(3, courts2.size());
        assertEquals(testCourt_F, courts2.get(0));
        assertEquals(testCourt_B, courts2.get(1));
        assertEquals(testCourt_T, courts2.get(2));

        assertEquals(0, cm.findCourts((c) -> c.getBaseCost() == 200).size());
    }
}
