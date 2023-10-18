import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;
import nbd.gV.old.OldRepository;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.CourtRepository;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtManagerTest {

    private final CourtRepository courtRepository = new CourtRepository("test");

    @AfterEach
    public void cleanDataBase(){
        List<Court> listOfCourts = courtRepository.findAll();
        for (Court court : listOfCourts) {
            courtRepository.delete(court);
        }
    }

    @Test
    void testCreatingCourtManager() {
        CourtManager courtManager = new CourtManager("test");
        assertNotNull(courtManager);
        assertEquals(0, courtManager.getAllCourts().size());
    }

    @Test
    void testRegisteringNewCourt() {
        CourtManager cm = new CourtManager("test");
        assertNotNull(cm);
        assertEquals(0, cm.getAllCourts().size());

        Court newCourt = cm.registerCourt(200, 200, 5);
        assertEquals(1, cm.getAllCourts().size());
        assertEquals(newCourt, cm.getCourt(newCourt.getCourtId()));
        assertThrows(CourtException.class,
                () -> cm.registerCourt(300, 300, 5));
        assertEquals(1, cm.getAllCourts().size());

        cm.registerCourt(200, 200, 6);
        cm.registerCourt(200, 200, 7);
        cm.registerCourt(200, 200, 8);
        assertEquals(4, cm.getAllCourts().size());
    }

    @Test
    void testGettingCourt() {
        CourtManager cm = new CourtManager("test");
        assertNotNull(cm);

        Court testCourt1 = cm.registerCourt(10,50,1);
        assertEquals(1, cm.getAllCourts().size());

        Court testCourt2 = cm.registerCourt(14,67,2);
        assertEquals(2, cm.getAllCourts().size());

        assertEquals(testCourt1, cm.getCourt(testCourt1.getCourtId()));
        assertEquals(testCourt2, cm.getCourt(testCourt2.getCourtId()));
        assertNull(cm.getCourt(UUID.randomUUID()));
    }

    @Test
    void testUnregisteringCourt() {
        CourtManager cm = new CourtManager("test");
        assertNotNull(cm);

        Court testCourt1 = cm.registerCourt(10,50,1);
        assertEquals(1, cm.getAllCourts().size());
        Court testCourt2 = cm.registerCourt(14,67,2);
        assertEquals(2, cm.getAllCourts().size());

        assertEquals(2, cm.getAllCourts().size());
        assertEquals(testCourt1, cm.getCourt(testCourt1.getCourtId()));
        assertFalse(testCourt1.isArchive());

        cm.unregisterCourt(testCourt1);

        assertEquals(2, cm.getAllCourts().size());
        Court dbCourt = cm.getCourt(testCourt1.getCourtId());
        assertTrue(dbCourt.isArchive());

        // Testujemy wyrejestrowanie klienta który nie nalezy do repozytorium
        Court testCourt3 = new Court(41,11,3);
        assertFalse(testCourt3.isArchive());

        assertThrows(CourtException.class, () -> cm.unregisterCourt(testCourt3));
        assertFalse(testCourt3.isArchive());
        assertEquals(2, cm.getAllCourts().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterCourt(null));
        assertEquals(2, cm.getAllCourts().size());
    }
}
