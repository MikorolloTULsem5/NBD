package nbd.gV.repositories;


import lombok.Getter;
import nbd.gV.mappers.CourtMapper;
import org.bson.conversions.Bson;
import java.util.List;
import java.util.UUID;

@Getter
public class CourtRepository extends CourtMongoRepository {

    CourtRedisRepository cache = new CourtRedisRepository();
    CourtMongoRepository db = new CourtMongoRepository();

    @Override
    public boolean create(CourtMapper mapper) {
        if (db.create(mapper)) {
            return cache.create(mapper);
        } else return false;
    }

    @Override
    public CourtMapper readByUUID(UUID uuid) {
        CourtMapper result = cache.read(uuid.toString());
        if (result != null) return result;
        else {
            result = db.readByUUID(uuid);
            if (result != null) {
                cache.create(result);
            }
            return result;
        }
    }

    @Override
    public List<CourtMapper> read(Bson filter) {
        List<CourtMapper> result = db.read(filter);
        if (result.size() < 10) {
            for (var mapper : result) {
                cache.create(mapper);
            }
        }
        return result;
    }

    @Override
    public boolean update(UUID uuid, String fieldName, Object value) {
        boolean result = db.update(uuid, fieldName, value);
        if (result) {
            CourtMapper updated = db.readByUUID(uuid);
            cache.create(updated);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(UUID uuid) {
        boolean result = db.delete(uuid);
        if (result) {
            cache.delete(uuid.toString());
            return true;
        }
        return false;
    }

}
