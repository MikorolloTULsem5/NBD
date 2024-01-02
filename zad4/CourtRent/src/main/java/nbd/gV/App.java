package nbd.gV;

import nbd.gV.clients.Client;
import nbd.gV.repositories.clients.ClientCassandraRepository;

import java.util.Random;

public class App {
    public static void main(String[] args) {
        try (ClientCassandraRepository acr = new ClientCassandraRepository()) {

            Client client = new Client(
                    "Adam",
                    "Smith",
                    String.valueOf(10_000_000_000L + (long) (new Random().nextDouble() * 89_999_999_999L)),
                    "athlete");

            acr.create(client);

            System.out.println("TEST1: " + acr.read("34831451710"));
            System.out.println("TEST2: " + acr.readByUUID("be2ec0ee-773d-43ed-9809-b7bb15f6ed52"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
