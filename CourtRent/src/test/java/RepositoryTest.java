import nbd.gV.BasketballCourt;
import nbd.gV.Court;
import nbd.gV.FootballCourt;
import nbd.gV.Repository;
import nbd.gV.TennisCourt;
import nbd.gV.VolleyballCourt;
import nbd.gV.exceptions.RepositoryException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositoryTest {
    Court c1, c2, c3;

    @BeforeEach
    void setUp() {
        c1 = new VolleyballCourt(1000, 100, 1);
        c2 = new FootballCourt(1000, 100, 2);
        c3 = null;
    }

    @Test
    void testCreatingRepository() {
        Repository<Court> repo = new Repository<>(Arrays.asList(c1, c2));
        assertNotNull(repo);

        assertEquals(2, repo.size());
        assertEquals(c1, repo.get(0));
        assertEquals(c2, repo.get(1));
    }

    @Test
    void testAdding() {
        Repository<Court> repo = new Repository<>();
        assertNotNull(repo);
        assertEquals(repo.size(), 0);

        assertTrue(repo.add(c1));
        assertEquals(repo.size(), 1);
        assertEquals(c1, repo.get(0));

        assertTrue(repo.add(c2));
        assertEquals(repo.size(), 2);
        assertEquals(c2, repo.get(1));

        assertFalse(repo.add(c3));
        assertEquals(repo.size(), 2);
        assertThrows(RepositoryException.class, () -> repo.get(2));
    }

    @Test
    void testRemoving() {
        Repository<Court> repo = new Repository<>();
        assertNotNull(repo);

        assertTrue(repo.add(c1));
        assertTrue(repo.add(c2));
        assertEquals(2, repo.size());

        assertTrue(repo.remove(c1));
        assertEquals(1, repo.size());

        assertFalse(repo.remove(c1));
        assertEquals(1, repo.size());

        assertFalse(repo.remove(null));
        assertEquals(1, repo.size());
    }

    @Test
    void testReporting() {
        Repository<Court> repo = new Repository<>(Arrays.asList(c1, c2));
        assertNotNull(repo);
        assertEquals(2, repo.size());

        assertEquals("Repozytorium zawiera obecnie 2 element/y/ow", repo.report());
    }

    @Test
    void testFinding() {
        Court c4 = new TennisCourt(1000, 120, 4);
        assertNotNull(c4);
        Repository<Court> repo = new Repository<>(Arrays.asList(c1, c2, c4));
        assertNotNull(repo);

        List<Court> list = repo.find((c) -> c.getBaseCost() == 100);
        assertEquals(2, list.size());
        assertEquals(c1, list.get(0));
        assertEquals(c2, list.get(1));

        List<Court> list2 = repo.find((c) -> c.getArea() == 1000);
        assertEquals(3, list2.size());
        assertEquals(c1, list2.get(0));
        assertEquals(c2, list2.get(1));
        assertEquals(c4, list2.get(2));

        List<Court> list3 = repo.find((c) -> c.getCourtTypeName().equals("BasketballCourt"));
        assertEquals(0, list3.size());
    }

    @Test
    void testFindingByUID() {
        Repository<Court> repo = new Repository<>(Arrays.asList(c1, c2));
        assertNotNull(repo);

        assertEquals(c1, repo.findByUID((c) -> c.getCourtNumber() == 1));
        assertEquals(c2, repo.findByUID((c) -> c.getCourtNumber() == 2));
        assertNull(repo.findByUID((c) -> c.getCourtNumber() == 3));
    }
}