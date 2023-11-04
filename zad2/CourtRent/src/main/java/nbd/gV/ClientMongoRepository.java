package nbd.gV;

import com.mongodb.client.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class ClientMongoRepository extends AbstractMongoRepository {

    public void create(ClientMapper clientMapper) {
        MongoCollection<ClientMapper> clientsCollection =
                getDatabase().getCollection(getCollectionName(), ClientMapper.class);
        clientsCollection.insertOne(clientMapper);
    }

    public List<ClientMapper> readAll() {
        MongoCollection<ClientMapper> clientsCollection =
                getDatabase().getCollection(getCollectionName(), ClientMapper.class);
        return clientsCollection.find().into(new ArrayList<>());
    }

    public String getCollectionName() {
        return "clients";
    }
}
