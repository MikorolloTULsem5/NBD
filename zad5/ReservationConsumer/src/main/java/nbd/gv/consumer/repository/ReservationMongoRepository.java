package nbd.gv.consumer.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;

import nbd.gv.consumer.model.Reservation;

import org.bson.Document;

import java.util.ArrayList;

public class ReservationMongoRepository extends AbstractMongoRepository<Reservation> {

    private static final String COLLECTION_NAME = "reservations";

    public ReservationMongoRepository(String dbName) {
        super(dbName);
        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME);
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "clientid",
                                        "courtid",
                                        "begintime"
                                    ],
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection(COLLECTION_NAME, createCollectionOptions);

//            getDatabase().createCollection(COLLECTION_NAME);
        }
    }

    @Override
    protected MongoCollection<Reservation> getCollection() {
        return getDatabase().getCollection(COLLECTION_NAME, Reservation.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }
}
