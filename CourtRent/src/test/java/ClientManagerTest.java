import nbd.gV.clients.Client;
import nbd.gV.clients.ClientManager;
import nbd.gV.Repository;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Normal;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientManagerTest {
    String testFirstName1;
    String testFirstName2;
    String testLastName1;
    String testLastName2;
    String testPersonalID;

    ClientType testClientType;
    Client testClient1;
    Client testClient2;
    Client testClient3;
    Repository<Client> clientRepository;

    @BeforeEach
    void setUp() {
        testFirstName1 = "John";
        testFirstName2 = "John";
        testLastName1 = "Johnny";
        testLastName2 = "Depp";
        testPersonalID = "12345678";
        testClientType = new Normal();
        testClient1 = new Client("Adam", "Smith", "54564211", testClientType);
        testClient2 = new Client("Eva", "Brown", "41565646", testClientType);
        testClient3 = new Client("Adam", "Long", "81657664", testClientType);
        clientRepository = new Repository<>();
    }

    @Test
    void testCreatingClientManager() {
        ClientManager cm = new ClientManager(clientRepository);
        assertNotNull(cm);
        assertEquals(0, cm.getAllClients().size());

        clientRepository.add(testClient1);
        assertEquals(1, cm.getAllClients().size());
    }

    @Test
    void testGettingClient() {
        clientRepository.add(testClient1);
        clientRepository.add(testClient2);
        ClientManager cm = new ClientManager(clientRepository);
        assertNotNull(cm);

        assertEquals(2, cm.getAllClients().size());
        assertEquals(testClient1, cm.getClient("54564211"));
        assertEquals(testClient2, cm.getClient("41565646"));
        assertNull(cm.getClient("00000000"));
        assertThrows(MainException.class, () -> cm.getClient(""));
    }
    @Test
    void testRegisteringNewClient() {
        ClientManager cm = new ClientManager(clientRepository);
        assertNotNull(cm);
        assertEquals(0, cm.getAllClients().size());

        Client newClient = cm.registerClient(testFirstName1, testLastName1, testPersonalID, testClientType);
        assertEquals(1, cm.getAllClients().size());
        assertEquals(newClient, cm.getClient(testPersonalID));
        assertThrows(ClientException.class,
                () -> cm.registerClient(testFirstName2, testLastName2, testPersonalID, testClientType));
        assertEquals(1, cm.getAllClients().size());
    }

    @Test
    void testCreatingClientManagerWithNullDate() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client newClient = cm.registerClient(testFirstName1, testLastName1, testPersonalID, testClientType);
        assertEquals(1, cm.getAllClients().size());
        assertEquals(newClient, cm.getClient(testPersonalID));
    }

    @Test
    void testUnregisteringClient() {
        clientRepository.add(testClient1);
        clientRepository.add(testClient2);
        ClientManager cm = new ClientManager(clientRepository);
        assertNotNull(cm);

        assertEquals(2, cm.getAllClients().size());
        assertEquals(testClient1, cm.getClient("54564211"));
        assertFalse(testClient1.isArchive());

        cm.unregisterClient(testClient1);

        assertEquals(1, cm.getAllClients().size());
        assertNull(cm.getClient("54564211"));
        assertTrue(testClient1.isArchive());

        // Testujemy wyrejestrowanie klienta który nie nalezy do repozytorium
        assertNull(cm.getClient("81657664"));
        assertFalse(testClient3.isArchive());

        assertThrows(ClientException.class, () -> cm.unregisterClient(testClient3));
        assertFalse(testClient3.isArchive());
        assertEquals(1, cm.getAllClients().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterClient(null));
        assertEquals(1, cm.getAllClients().size());
    }

    @Test
    void testFindingClients() {
        clientRepository.add(testClient1);
        clientRepository.add(testClient2);
        clientRepository.add(testClient3);
        ClientManager cm = new ClientManager(clientRepository);
        assertNotNull(cm);

        List<Client> clients1 = cm.findClients((c) -> c.getFirstName().equals("Adam"));
        assertEquals(2, clients1.size());
        assertEquals(testClient1, clients1.get(0));
        assertEquals(testClient3, clients1.get(1));

        List<Client> clients2 = cm.findClients((c) -> c.getClientType() == testClientType);
        assertEquals(3, clients2.size());
        assertEquals(testClient1, clients2.get(0));
        assertEquals(testClient2, clients2.get(1));
        assertEquals(testClient3, clients2.get(2));

        assertEquals(0, cm.findClients((c) -> c.getLastName().equals("Dlugosz")).size());
    }
}
