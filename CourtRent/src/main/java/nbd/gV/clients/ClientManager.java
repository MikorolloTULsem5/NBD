package nbd.gV.clients;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.ClientRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientRepository clientRepository;

    private List<ClientType> listOfTypes = Arrays.asList(new Normal(), new Athlete(), new Coach());

    public ClientManager(String unitName) {
        this.clientRepository = new ClientRepository(unitName);
    }

    public ClientManager() {
        this.clientRepository = new ClientRepository("default");
    }

//    private ClientType returnClientTypeFromDB(ClientType clientType) {
//        CriteriaBuilder cb = clientRepository.getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<Client> query = cb.createQuery(Client.class);
//        Root<Client> clientRoot = query.from(Client.class);
//        query.select(clientRoot).where(cb.equal(clientRoot.get(Client_.CLIENT_TYPE), clientType));
//        List<Client> result = clientRepository.find(query);
//
//        return result.isEmpty() ? null : result.get(0).getClientType();
//    }

    public Client registerClient(String firstName, String lastName, String personalID, ClientType clientType) {
        Client newClient = new Client(firstName, lastName, personalID, clientType);
        try {
//            ClientType clientTypeFromDB = returnClientTypeFromDB(clientType);
//
//            if (clientTypeFromDB == null) {
//                clientRepository.create(newClient);
//            } else {
//                newClient.setClientType(clientTypeFromDB);
//                clientRepository.create(newClient);
//            }
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
            client.setArchive(false);
            throw new ClientException("Nie udalo sie wyrejestrowac podanego boiska.");
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
