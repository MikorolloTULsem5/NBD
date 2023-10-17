package nbd.gV;


import nbd.gV.clients.Client;
import nbd.gV.clients.Normal;
import nbd.gV.clients.ClientManager;

public class Main {
    public static void main(String[] args) {
        ClientManager cm = new ClientManager();
        Client c1 = cm.registerClient("Michal", "Ziomal", "12345678", new Normal());
        Client c2 = cm.registerClient("Adam", "Niezgodka", "675678858", new Normal());

        cm.unregisterClient(c1);
    }
}