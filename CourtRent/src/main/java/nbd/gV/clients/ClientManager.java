package nbd.gV.clients;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.ClientRepository;

import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientRepository clientRepository;

    public ClientManager(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientManager() {
        this(new ClientRepository("default"));
    }

    public Client registerClient(String firstName, String lastName, String personalID, ClientType clientType) {
        Client newClient = new Client(firstName, lastName, personalID, clientType);
        try {
            clientRepository.create(newClient);
        } catch (JakartaException exception) {
            throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie!");
        }
        return newClient;
    }

    public void unregisterClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        try {
            client.setArchive(true);
            clientRepository.update(client);
        } catch (JakartaException exception) {
            throw new JakartaException(("Nie udalo sie wyrejestrowac podanego klienta!"));
        }
    }

    public Client getClient(UUID clientID) {
        try {
            return clientRepository.findByUUID(clientID);
        } catch (JakartaException exception) {
            throw new ClientException("Blad transakcji.");
        }
    }

    public List<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (JakartaException exception) {
            throw new CourtException("Nie udalo sie uzyskac clientow.");
        }
    }

    public Client findCourtByPersonalId(String personalId) {
        CriteriaBuilder cb = clientRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Client> query = cb.createQuery(Client.class);
        Root<Client> clientRoot = query.from(Client.class);
        query.select(clientRoot).where(cb.equal(clientRoot.get(Client_.PERSONAL_ID), personalId));
        List<Client> result = clientRepository.find(query);
        return result.isEmpty() ? null : result.get(0);
    }
}
