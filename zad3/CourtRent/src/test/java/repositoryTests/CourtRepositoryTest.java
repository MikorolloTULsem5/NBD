package repositoryTests;

import com.mongodb.client.model.Filters;
import nbd.gV.courts.Court;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.repositories.CourtMongoRepository;
import nbd.gV.repositories.CourtRedisRepository;
import nbd.gV.repositories.CourtRepository;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CourtRepositoryTest {
    static final CourtRepository courtRepository = new CourtRepository();
    static final CourtMongoRepository db = courtRepository.getDb();
    static final CourtRedisRepository cache = courtRepository.getCache();
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
        db.delete(court1.getCourtId());
        db.delete(court2.getCourtId());
        db.delete(court3.getCourtId());

        cache.delete(courtMapper1.getCourtId());
        cache.delete(courtMapper2.getCourtId());
        cache.delete(courtMapper3.getCourtId());
    }

    @Test
    void createReadByUUIDFromCacheCourtRepositoryTest(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);

        assertEquals(courtMapper1, courtRepository.getCache().read(courtMapper1.getCourtId()));
        assertEquals(courtMapper2, courtRepository.getCache().read(courtMapper2.getCourtId()));
        assertEquals(courtMapper3, courtRepository.getCache().read(courtMapper3.getCourtId()));

        assertEquals(courtMapper1,courtRepository.readByUUID(court1.getCourtId()));
        assertEquals(courtMapper2,courtRepository.readByUUID(court2.getCourtId()));
        assertEquals(courtMapper3,courtRepository.readByUUID(court3.getCourtId()));
    }

    @Test
    void createReadByUUIDFromDBCourtRepositoryTest(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);

        assertTrue(courtRepository.getCache().delete(courtMapper1.getCourtId()));
        assertTrue(courtRepository.getCache().delete(courtMapper2.getCourtId()));
        assertTrue(courtRepository.getCache().delete(courtMapper3.getCourtId()));

        assertNull(courtRepository.getCache().read(courtMapper1.getCourtId()));
        assertNull(courtRepository.getCache().read(courtMapper2.getCourtId()));
        assertNull(courtRepository.getCache().read(courtMapper3.getCourtId()));

        assertEquals(courtMapper1,courtRepository.readByUUID(court1.getCourtId()));
        assertEquals(courtMapper2,courtRepository.readByUUID(court2.getCourtId()));
        assertEquals(courtMapper3,courtRepository.readByUUID(court3.getCourtId()));
    }

    @Test
    void readCourtRepositoryTest(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);
        var courtsList = courtRepository.read(Filters.eq("area", 300));

        assertNotNull(courtsList);
        assertEquals(courtMapper3, courtsList.get(0));
        assertEquals(courtMapper3, courtRepository.getCache().read(courtMapper3.getCourtId()));
    }

    @Test
    void readNonexistentCourtRepositoryTest(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);
        var courtsList = courtRepository.read(Filters.eq("area", 311));

        assertEquals(0,courtsList.size());
        assertNull(courtRepository.readByUUID(UUID.randomUUID()));
    }

    @Test
    void deleteCourtRepositoryTest(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);

        assertTrue(courtRepository.delete(court1.getCourtId()));

        assertNull(cache.read(courtMapper1.getCourtId()));
        assertNull(db.readByUUID(court1.getCourtId()));
        assertNull(courtRepository.readByUUID(court1.getCourtId()));
    }

    @Test
    void deleteOnlyDBCourtRepositoryTest(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);

        courtRepository.delete(court1.getCourtId());
        cache.delete(courtMapper1.getCourtId());
        assertNull(cache.read(courtMapper1.getCourtId()));
        assertNull(db.readByUUID(court1.getCourtId()));
        assertNull(courtRepository.readByUUID(court1.getCourtId()));
    }

    @Test
    void readByUUIDDisconnectedCache(){
        courtRepository.create(courtMapper1);
        courtRepository.create(courtMapper2);
        courtRepository.create(courtMapper3);

        cache.close();

        assertEquals(courtMapper1,courtRepository.readByUUID(court1.getCourtId()));
        assertEquals(courtMapper2,courtRepository.readByUUID(court2.getCourtId()));
        assertEquals(courtMapper3,courtRepository.readByUUID(court3.getCourtId()));
        cache.connect();
    }
}
