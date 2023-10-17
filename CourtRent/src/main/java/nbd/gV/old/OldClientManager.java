package nbd.gV.old;

import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import nbd.gV.old.OldRepository;

import java.util.List;
import java.util.function.Predicate;

public class OldClientManager {

    private OldRepository<Client> clients;

    public OldClientManager(OldRepository<Client> clients) {
        this.clients = clients;
    }

    public OldClientManager() {
        this(new OldRepository<>());
    }

    public Client registerClient(String firstName, String lastName, String personalID, ClientType clientType) {
        if (clients.findByUID((c) -> c.getPersonalID().equals(personalID)) == null) {
            Client newClient = new Client(firstName, lastName, personalID, clientType);
            clients.add(newClient);
            return newClient;
        } else {
            throw new ClientException("Klient o tym numerze PESEL juz istnieje w repozytorium!");
        }
    }

    public void unregisterClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        if (clients.findByUID((c) -> c.getPersonalID().equals(client.getPersonalID())) != null) {
            if (clients.remove(client)) {
                client.setArchive(true);
            }
        } else {
            throw new ClientException("Podany do wyrejestrowania klient, nie znajduje sie w repozytorium!");
        }
    }

    public Client getClient(String personalID) {
        if (personalID.isEmpty()) {
            throw new MainException("Podano niewlasciwy numer PESEL - pole jest puste");
        }
        return clients.findByUID((c) -> c.getPersonalID().equals(personalID));
    }

    public List<Client> findClients(Predicate<Client> predicate) {
        return clients.find(predicate);
    }

    public List<Client> getAllClients() {
        return clients.find((c) -> true);
    }
}
