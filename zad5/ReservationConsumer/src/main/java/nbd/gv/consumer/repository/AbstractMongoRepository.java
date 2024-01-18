package nbd.gv.consumer.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractMongoRepository<T> implements AutoCloseable {

    private final ConnectionString connectionString = new ConnectionString(
            "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=replica_set_single"
    );
    private final MongoCredential credential = MongoCredential.createCredential("admin", "admin",
            "adminpassword".toCharArray());

    private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
            .automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
            .build());
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public AbstractMongoRepository(String dbName) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase(dbName);
    }

    protected MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    /*----------------------------------------------CRUD-------------------------------------------------------*/

    public void create(T mapper) {
        try {
            this.getCollection().insertOne(mapper);
        } catch (MongoWriteException e) {
            e.getMessage();
        }
    }

    public List<T> read(Bson filter) {
        return this.getCollection().find(filter).into(new ArrayList<>());
    }

    public List<T> readAll() {
        return this.read(Filters.empty());
    }

    public T readByUUID(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter);
        return !list.isEmpty() ? list.get(0) : null;
    }

    protected MongoCollection<T> getCollection() {
        return null;
    }

    public String getCollectionName() {
        return null;
    }
}
