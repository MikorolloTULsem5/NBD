package nbd.gV.clients;

import com.mongodb.client.model.Filters;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.repositories.ClientMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientMongoRepository clientRepository;

    public ClientManager() {
        this.clientRepository = new ClientMongoRepository();
    }

    public Client registerClient(String firstName, String lastName, String personalID, ClientType clientType) {
        Client newClient = new Client(firstName, lastName, personalID, clientType);
        try {
            if (!clientRepository.create(ClientMapper.toMongoClient(newClient))) {
                throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
            }
        } catch (MyMongoException exception) {
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
            if (!clientRepository.update(client.getClientID(), "archive", true)) {
                client.setArchive(false);
                throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            client.setArchive(false);
            throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta. - nieznany blad");
        }
    }

    public Client getClient(UUID clientID) {
        try {
            return ClientMapper.fromMongoClient(clientRepository.readByUUID(clientID));
        } catch (JakartaException exception) {
            throw new ClientException("Blad transakcji.");
        }
    }

    public List<Client> getAllClients() {
        try {
            List<Client> clientsList = new ArrayList<>();
            for (var el :  clientRepository.readAll()) {
                clientsList.add(ClientMapper.fromMongoClient(el));
            }
            return clientsList;
        } catch (Exception exception) {
            throw new ClientException("Nie udalo sie uzyskac clientow.");
        }
    }

    public Client findCourtByPersonalId(String personalId) {
        return ClientMapper.fromMongoClient(
                clientRepository.read(Filters.eq("personalid", personalId)).get(0));
    }
}
