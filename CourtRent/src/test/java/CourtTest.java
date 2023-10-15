import nbd.gV.courts.BasketballCourt;
import nbd.gV.courts.Court;
import nbd.gV.courts.FootballCourt;
import nbd.gV.courts.TennisCourt;
import nbd.gV.courts.VolleyballCourt;
import nbd.gV.exceptions.MainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtTest {

    @Test
    void testCreatingCourt() {
        Court newCourt1 = new FootballCourt(300, 100, 1);
        assertNotNull(newCourt1);

        assertEquals(300, newCourt1.getArea());
        assertEquals(100, newCourt1.getBaseCost());
        assertEquals(1, newCourt1.getCourtNumber());
        assertFalse(newCourt1.isArchive());
        assertFalse(newCourt1.isRented());

        assertThrows(MainException.class, () -> new FootballCourt(0, 100, 99));
        assertThrows(MainException.class, () -> new FootballCourt(1, -100, 99));
        assertThrows(MainException.class, () -> new FootballCourt(1, 100, 0));

        Court newCourt2 = new BasketballCourt(300, 100, 2);
        assertNotNull(newCourt2);
        Court newCourt3 = new TennisCourt(300, 100, 3);
        assertNotNull(newCourt3);
        Court newCourt4 = new VolleyballCourt(300, 100, 4);
        assertNotNull(newCourt4);
    }

    @Test
    void testSetters() {
        Court newCourt = new FootballCourt(300, 100, 1);
        assertNotNull(newCourt);

        assertEquals(300, newCourt.getArea());
        newCourt.setArea(0);
        assertEquals(300, newCourt.getArea());
        newCourt.setArea(20);
        assertEquals(20, newCourt.getArea());

        assertEquals(100, newCourt.getBaseCost());
        newCourt.setBaseCost(-1);
        assertEquals(100, newCourt.getBaseCost());
        newCourt.setBaseCost(10);
        assertEquals(10, newCourt.getBaseCost());


        assertFalse(newCourt.isArchive());
        newCourt.setArchive(true);
        assertTrue(newCourt.isArchive());
        newCourt.setArchive(false);
        assertFalse(newCourt.isArchive());

        assertFalse(newCourt.isRented());
        newCourt.setRented(true);
        assertTrue(newCourt.isRented());
        newCourt.setRented(false);
        assertFalse(newCourt.isRented());
    }

    @Test
    void testGettingActualReservationPrice() {
        Court newCourt1 = new FootballCourt(300, 100, 1);
        assertNotNull(newCourt1);
        Court newCourt2 = new BasketballCourt(300, 100, 2);
        assertNotNull(newCourt2);
        Court newCourt3 = new TennisCourt(300, 100, 3);
        assertNotNull(newCourt3);
        Court newCourt4 = new VolleyballCourt(300, 100, 4);
        assertNotNull(newCourt4);

        assertEquals(150, (int) newCourt1.getActualReservationPrice());
        assertEquals(130, (int) newCourt2.getActualReservationPrice());
        assertEquals(110, (int) newCourt3.getActualReservationPrice());
        assertEquals(120, (int) newCourt4.getActualReservationPrice());

        assertEquals("FootballCourt", newCourt1.getCourtTypeName());
        assertEquals("BasketballCourt", newCourt2.getCourtTypeName());
        assertEquals("TennisCourt", newCourt3.getCourtTypeName());
        assertEquals("VolleyballCourt", newCourt4.getCourtTypeName());
    }

    @Test
    void testGettingCourtTypeNamesAndInfo() {
        Court newCourt1 = new FootballCourt(300, 100, 1);
        assertNotNull(newCourt1);
        Court newCourt2 = new BasketballCourt(300, 100, 2);
        assertNotNull(newCourt2);
        Court newCourt3 = new TennisCourt(300, 100, 3);
        assertNotNull(newCourt3);
        Court newCourt4 = new VolleyballCourt(300, 100, 4);
        assertNotNull(newCourt4);

        assertEquals("FootballCourt", newCourt1.getCourtTypeName());
        assertEquals("BasketballCourt", newCourt2.getCourtTypeName());
        assertEquals("TennisCourt", newCourt3.getCourtTypeName());
        assertEquals("VolleyballCourt", newCourt4.getCourtTypeName());

        assertEquals("Boisko nr 1 przeznaczone do pilki noznej o powierzchni 300,00 i koszcie " +
                        "za rezerwacje: 150,00 PLN\n", newCourt1.getCourtInfo());
        assertEquals("Boisko nr 2 przeznaczone do koszykowki o powierzchni 300,00 i koszcie " +
                "za rezerwacje: 130,00 PLN\n", newCourt2.getCourtInfo());
        assertEquals("Boisko nr 3 przeznaczone do tenisa o powierzchni 300,00 i koszcie " +
                "za rezerwacje: 110,00 PLN\n", newCourt3.getCourtInfo());
        assertEquals("Boisko nr 4 przeznaczone do siatkowki o powierzchni 300,00 i koszcie " +
                "za rezerwacje: 120,00 PLN\n", newCourt4.getCourtInfo());
    }
}
