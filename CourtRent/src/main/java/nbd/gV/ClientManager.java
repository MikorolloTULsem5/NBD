package nbd.gV;

import nbd.gV.clientstype.ClientType;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;

import java.util.List;
import java.util.function.Predicate;

public class ClientManager {

    private Repository<Client> clients;

    public ClientManager(Repository<Client> clients) {
        this.clients = clients;
    }

    public ClientManager() {
        this(new Repository<>());
    }

    public Client registerClient(String firstName, String lastName, String personalID, ClientType clientType) {
        if (clients.findByUID((c) -> c.getPersonalID().equals(personalID)) == null) {
            Client newClient = new Client(firstName, lastName, personalID, clientType);
            if (!(clients.add(newClient))) {
                throw new ClientException("Nowy klient nie zostal zarejestrowany!");
            } ///TODO czy taki przypadek jest wogole mozliwy
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
