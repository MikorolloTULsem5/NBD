package nbd.gV;

import nbd.gV.clients.Client;
import nbd.gV.clients.Normal;

public class Main {

    public static void main(String[] args) {
        try (var repo = new ClientMongoRepository()) {
            Client client = new Client("Adam", "Szulc", "12345678901", new Normal());
            ClientMapper clientMapper = new ClientMapper(
                    client.getClientID().toString(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getPersonalId(),
                    client.isArchive(),
                    client.getClientType().getClientTypeName()
            );

            repo.add(clientMapper);
        } catch (Exception e) {
            System.out.println("test");
        }
    }
}
