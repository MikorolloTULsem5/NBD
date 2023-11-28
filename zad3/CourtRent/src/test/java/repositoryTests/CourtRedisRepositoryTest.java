package repositoryTests;

import nbd.gV.courts.Court;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.repositories.CourtRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CourtRedisRepositoryTest {
    static final CourtRedisRepository courtRedisRepository = new CourtRedisRepository();
    CourtMapper courtMapper1;
    CourtMapper courtMapper2;
    CourtMapper courtMapper3;
    Court court1;
    Court court2;
    Court court3;

    @BeforeEach
    void initData() {
        court1 = new Court(100, 200, 1);
        courtMapper1 = CourtMapper.toMongoCourt(court1);

        court2 = new Court(200, 200, 2);
        courtMapper2 = CourtMapper.toMongoCourt(court2);

        court3 = new Court(300, 300, 3);
        courtMapper3 = CourtMapper.toMongoCourt(court3);
    }

    @AfterEach
    void clearDB(){
        courtRedisRepository.delete(court1.getCourtId().toString());
        courtRedisRepository.delete(court2.getCourtId().toString());
        courtRedisRepository.delete(court3.getCourtId().toString());
    }

    @Test
    void createCourtSuccessfulTest(){
        assertTrue(courtRedisRepository.create(courtMapper1));
        assertTrue(courtRedisRepository.create(courtMapper2));
        assertTrue(courtRedisRepository.create(courtMapper3));
    }

    @Test
    void readCourtTest(){
        assertTrue(courtRedisRepository.create(courtMapper1));
        assertTrue(courtRedisRepository.create(courtMapper2));
        assertTrue(courtRedisRepository.create(courtMapper3));

        CourtMapper testCourtMapper1 = courtRedisRepository.read(court1.getCourtId().toString());
        CourtMapper testCourtMapper2 = courtRedisRepository.read(court2.getCourtId().toString());

        assertEquals(courtMapper1, testCourtMapper1);
        assertEquals(courtMapper2, testCourtMapper2);
    }

    @Test
    void readNonexistentCourtTest(){
        assertTrue(courtRedisRepository.create(courtMapper1));
        assertTrue(courtRedisRepository.create(courtMapper2));
        assertTrue(courtRedisRepository.create(courtMapper3));

        CourtMapper testCourtMapper1 = courtRedisRepository.read("a");
        assertNull(testCourtMapper1);
    }

    @Test
    void updateCourtSuccessfulTest(){
        assertTrue(courtRedisRepository.create(courtMapper1));
        assertTrue(courtRedisRepository.create(courtMapper2));
        assertTrue(courtRedisRepository.create(courtMapper3));

        court1.setArea(12);
        assertTrue(courtRedisRepository.create(CourtMapper.toMongoCourt(court1)));
        assertEquals(court1, CourtMapper.fromMongoCourt(courtRedisRepository.read(court1.getCourtId().toString())));

    }

}
