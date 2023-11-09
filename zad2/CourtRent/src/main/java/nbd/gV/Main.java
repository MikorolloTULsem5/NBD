package nbd.gV;

import com.mongodb.client.model.Filters;
import nbd.gV.clients.Client;
import nbd.gV.clients.Normal;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.repositories.ClientMongoRepository;
import nbd.gV.repositories.CourtMongoRepository;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;

public class Main {

//    public static void main(String[] args) {
//        try (var repo = new ClientMongoRepository()) {
//            Client client = new Client("Adam", "Szulc", "12345678901", new Normal());
//            ClientMapper clientMapper = new ClientMapper(
//                    client.getClientID().toString(),
//                    client.getFirstName(),
//                    client.getLastName(),
//                    client.getPersonalId(),
//                    client.isArchive(),
//                    client.getClientType().getClientTypeName()
//            );
//
//            repo.create(clientMapper);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
    }
}
