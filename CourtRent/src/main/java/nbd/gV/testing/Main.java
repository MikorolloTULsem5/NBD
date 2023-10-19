package nbd.gV.testing;

import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.ClientManager;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Coach;
import nbd.gV.clients.Normal;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ClientManager cm = new ClientManager();
        ClientType clientType1 = new Normal();
        ClientType clientType2 = new Athlete();
        ClientType clientType3 = new Coach();
        ClientType clientType4 = new Normal();
        ClientType clientType5 = new Normal();
        ClientType clientType6 = new Coach();
        ClientType clientType7 = new Coach();
        ClientType clientType8 = new Athlete();
        ClientType clientType9 = new Coach();
        ClientType clientType10 = new Athlete();
        ClientType clientType11 = new Coach();
        ClientType clientType12 = new Normal();
        Client client1 = cm.registerClient("Adama", "Smith", "12345678901", clientType1);
        Client client2 = cm.registerClient("Ewa", "Brown", "12345678902", clientType2);
        Client client3 = cm.registerClient("John", "Lenon", "12345678903", clientType3);
        Client client4 = cm.registerClient("John", "Lenon", "12345678904", clientType4);
        Client client5 = cm.registerClient("John", "Lenon", "12345678905", clientType5);
        Client client6 = cm.registerClient("John", "Lenon", "12345678906", clientType6);
        Client client7 = cm.registerClient("John", "Lenon", "12345678907", clientType7);
        Client client8 = cm.registerClient("John", "Lenon", "12345678908", clientType8);
        Client client9 = cm.registerClient("John", "Lenon", "12345678909", clientType9);
        Client client10 = cm.registerClient("John", "Lenon", "12345678910", clientType10);
        Client client11 = cm.registerClient("John", "Lenon", "12345678911", clientType11);
        Client client12 = cm.registerClient("John", "Lenon", "12345678912", clientType12);

        var list = List.of(client1, client2, client3, client4, client5, client6, client7, client8,
                client9, client10, client11, client12);

        list.forEach((client -> System.out.println(client.getClientType())));
        System.out.println("-".repeat(30));

        List<Client> list_DB = cm.getAllClients();
        list_DB.forEach((client -> System.out.println(client.getClientType())));
    }
}
