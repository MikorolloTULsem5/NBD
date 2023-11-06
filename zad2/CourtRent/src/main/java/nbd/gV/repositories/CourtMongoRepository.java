package nbd.gV.repositories;

import com.mongodb.client.MongoCollection;
import nbd.gV.mappers.CourtMapper;

public class CourtMongoRepository extends AbstractMongoRepository<CourtMapper> {

    @Override
    protected MongoCollection<CourtMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), CourtMapper.class);
    }

    @Override
    public String getCollectionName() {
        return "courts";
    }
}
