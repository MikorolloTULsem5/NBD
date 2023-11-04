package nbd.gV;

import com.mongodb.client.MongoCollection;

public class ClientMongoRepository extends AbstractMongoRepository {

    public void add(ClientMapper clientMapper) {
        MongoCollection<ClientMapper> clientsCollection =
                getDatabase().getCollection(getCollectionName(), ClientMapper.class);
        clientsCollection.insertOne(clientMapper);
    }

    public String getCollectionName() {
        return "clients";
    }
}
