package nbd.gV;

import nbd.gV.clients.Client;
import nbd.gV.repositories.clients.ClientCassandraRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        List<Client> clients = new ArrayList<>();
        try (ClientCassandraRepository acr = new ClientCassandraRepository()) {

            Client client = new Client(
                    "John",
                    "Smith",
                    String.valueOf(10_000_000_000L + (long) (new Random().nextDouble() * 89_999_999_999L)),
                    "athlete");

            acr.create(client);

            System.out.println("TEST1: " + acr.read("98824917721"));
            System.out.println("TEST2: " + acr.readByUUID("d5b5d57f-d208-493c-9097-88e2d72434c9"));
            clients = acr.readAll();

            client = acr.read("17778933802");
            client.setArchive(true);
            acr.update(client);

            acr.delete(acr.read("80064422555"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        System.out.println("SIZE: " + clients.size());
        clients.forEach(System.out::println);
    }
}
