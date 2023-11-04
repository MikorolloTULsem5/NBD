package nbd.gV;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientMongoRepository extends AbstractMongoRepository {

    public boolean create(ClientMapper clientMapper) {
        var result = this.getCollection().insertOne(clientMapper);
        return result.wasAcknowledged();
    }

    public List<ClientMapper> read(Bson filter) {
        return this.getCollection().find(filter).into(new ArrayList<>());
    }

    public List<ClientMapper> readAll() {
        return this.read(Filters.empty());
    }

    public ClientMapper readByUUID(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        return this.read(filter).get(0);
    }

    public boolean update(UUID uuid, String fieldName, Object value) {
        Bson filter = Filters.eq("_id", uuid.toString());
        Bson setUpdate = Updates.set(fieldName, value);
        UpdateResult result = this.getCollection().updateOne(filter, setUpdate);
        return result.getModifiedCount() != 0;
    }

    public boolean delete(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var deletedObj = this.getCollection().findOneAndDelete(filter);
        return deletedObj != null;
    }

    private MongoCollection<ClientMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), ClientMapper.class);
    }

    public String getCollectionName() {
        return "clients";
    }
}
