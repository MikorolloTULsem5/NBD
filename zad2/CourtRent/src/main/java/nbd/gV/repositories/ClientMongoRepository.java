package nbd.gV.repositories;

import com.mongodb.client.MongoCollection;
import nbd.gV.mappers.ClientMapper;

public class ClientMongoRepository extends AbstractMongoRepository<ClientMapper> {

    @Override
    protected MongoCollection<ClientMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), ClientMapper.class);
    }

    @Override
    public String getCollectionName() {
        return "clients";
    }
}
