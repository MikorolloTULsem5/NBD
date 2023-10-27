import nbd.gV.clients.Client;
import nbd.gV.clients.ClientManager;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Normal;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientManagerTest {

    private final ClientRepository clientRepository = new ClientRepository("test");
    private final ClientType testClientType = new Normal();
    @AfterEach
    void cleanDataBase(){
        List<Client> listOfClients = clientRepository.findAll();
        listOfClients.forEach(clientRepository::delete);
    }

    @Test
    void testCreatingClientManager() {
        ClientManager clientManager = new ClientManager("test");
        assertNotNull(clientManager);
        assertEquals(0, clientManager.getAllClients().size());
    }

    @Test
    void testRegisteringNewCourt() {
        ClientManager cm = new ClientManager("test");
        assertNotNull(cm);
        assertEquals(0, cm.getAllClients().size());

        Client newClient = cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(newClient);
        assertEquals(1, cm.getAllClients().size());
        assertEquals(newClient, cm.getClient(newClient.getClientID()));

        cm.registerClient("Adam", "Long", "12345678902", testClientType);
        cm.registerClient("Eva", "Brown" , "12345678903", testClientType);
        cm.registerClient("Adam", "Brown" , "12345678904", testClientType);
        assertEquals(4, cm.getAllClients().size());

        assertThrows(ClientException.class, () -> cm.registerClient("Eva", "Brown", "12345678901", testClientType));
        assertEquals(4, cm.getAllClients().size());
    }

    @Test
    void testGettingClient() {
        ClientManager cm = new ClientManager("test");
        assertNotNull(cm);

        Client testClient1 = cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());

        Client testClient2 = cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(testClient1, cm.getClient(testClient1.getClientID()));
        assertEquals(testClient2, cm.getClient(testClient2.getClientID()));
        assertNull(cm.getClient(UUID.randomUUID()));
    }

    @Test
    void testUnregisteringClient() {
        ClientManager cm = new ClientManager("test");
        assertNotNull(cm);

        Client testClient1 = cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 = cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(2, cm.getAllClients().size());
        assertEquals(testClient1, cm.getClient(testClient1.getClientID()));
        assertFalse(testClient1.isArchive());

        cm.unregisterClient(testClient1);

        assertEquals(2, cm.getAllClients().size());
        Client dbClient = cm.getClient(testClient1.getClientID());
        assertNotNull(dbClient);
        assertTrue(dbClient.isArchive());

        // Testujemy wyrejestrowanie boiska ktore nie nalezy do repozytorium
        Client testClient3 = new Client("John", "Lenon", "12345678903", testClientType);
        assertNotNull(testClient3);
        assertFalse(testClient3.isArchive());

        assertThrows(ClientException.class, () -> cm.unregisterClient(testClient3));
        assertFalse(testClient3.isArchive());
        assertEquals(2, cm.getAllClients().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterClient(null));
        assertEquals(2, cm.getAllClients().size());
    }

    @Test
    public void testFindCourtByPersonalId(){
        ClientManager cm = new ClientManager("test");
        assertNotNull(cm);

        Client testClient1 = cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 = cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        Client newClient = cm.findCourtByPersonalId("12345678901");
        assertNotNull(newClient);
        assertEquals(testClient1, newClient);
        Client newClient2 = cm.findCourtByPersonalId("12345678999");
        assertNull(newClient2);
    }
}
