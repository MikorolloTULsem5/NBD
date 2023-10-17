package nbd.gV.clients;


import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;

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
        clientRepository.add(newClient);
        return newClient;
    }

    public void unregisterClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        clientRepository.remove(client);
        client.setArchive(true);
    }

//    public Client getClient(String personalID) {
//        if (personalID.isEmpty()) {
//            throw new MainException("Podano niewlasciwy numer PESEL - pole jest puste");
//        }
//        return clients.findByUID((c) -> c.getPersonalID().equals(personalID));
//    }
//
//    public List<Client> findClients(Predicate<Client> predicate) {
//        return clients.find(predicate);
//    }
//
//    public List<Client> getAllClients() {
//        return clients.find((c) -> true);
//    }
}
