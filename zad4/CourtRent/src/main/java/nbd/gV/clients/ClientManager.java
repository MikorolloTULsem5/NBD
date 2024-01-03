package nbd.gV.clients;

import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.clients.ClientCassandraRepository;

import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientCassandraRepository clientRepository;

    public ClientManager() {
        this.clientRepository = new ClientCassandraRepository();
    }

    public Client registerClient(String firstName, String lastName, String personalID, String clientType) {
        Client newClient = new Client(firstName, lastName, personalID, clientType);
        if (clientRepository.read(personalID) != null) {
            throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym numerze PESEL" +
                    "znajduje sie juz w bazie");
        }

        clientRepository.create(newClient);

        return newClient;
    }

    public void unregisterClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        try {
            client.setArchive(true);
            clientRepository.update(client);
        } catch (Exception exception) {
            client.setArchive(false);
            throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta. - " + exception.getMessage());
        }
    }

    public Client getClient(UUID clientID) {
        return clientRepository.readByUUID(clientID);
    }

    public List<Client> getAllClients() {
        return clientRepository.readAll();
    }

    public Client getClientByPersonalId(String personalId) {
        return clientRepository.read(personalId);
    }
}
