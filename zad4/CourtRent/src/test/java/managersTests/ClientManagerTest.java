package managersTests;

import nbd.gV.clients.Client;
import nbd.gV.clients.ClientManager;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.clients.ClientCassandraRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientManagerTest {
    static final ClientCassandraRepository clientRepository = new ClientCassandraRepository();
    final String testClientType = "normal";

    @BeforeAll
    @AfterAll
    static void cleanDatabaseFirstAndLastTime() {
        clientRepository.readAll().forEach(clientRepository::delete);
    }

    @BeforeEach
    void cleanDatabase() {
        cleanDatabaseFirstAndLastTime();
    }

    @Test
    void testCreatingClientManager() {
        ClientManager clientManager = new ClientManager();
        assertNotNull(clientManager);
        assertEquals(0, clientManager.getAllClients().size());
    }

    @Test
    void testRegisteringNewCourt() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);
        assertEquals(0, cm.getAllClients().size());

        Client newClient =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(newClient);
        assertEquals(1, cm.getAllClients().size());
        assertEquals(newClient, cm.getClient(newClient.getClientId()));

        cm.registerClient("Adam", "Long", "12345678902", testClientType);
        cm.registerClient("Eva", "Brown", "12345678903", testClientType);
        cm.registerClient("Adam", "Brown", "12345678904", testClientType);
        assertEquals(4, cm.getAllClients().size());

        assertThrows(ClientException.class,
                () -> cm.registerClient("Eva", "Brown", "12345678901", testClientType));
        assertEquals(4, cm.getAllClients().size());
    }

    @Test
    void testGettingClient() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());

        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(testClient1, cm.getClient(testClient1.getClientId()));
        assertEquals(testClient2, cm.getClient(testClient2.getClientId()));
        assertNull(cm.getClient(UUID.randomUUID()));
    }

    @Test
    void testUnregisteringClient() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        assertEquals(2, cm.getAllClients().size());
        assertEquals(testClient1, cm.getClient(testClient1.getClientId()));
        assertFalse(testClient1.isArchive());

        cm.unregisterClient(testClient1);

        assertEquals(2, cm.getAllClients().size());
        Client dbClient = cm.getClient(testClient1.getClientId());
        assertNotNull(dbClient);
        assertTrue(dbClient.isArchive());

        // Testujemy wyrejestrowanie klienta ktore nie nalezy do repozytorium
        Client testClient3 = new Client("John", "Lenon", "12345678903", testClientType);
        assertNotNull(testClient3);
        assertFalse(testClient3.isArchive());

        assertFalse(testClient3.isArchive());
        assertEquals(2, cm.getAllClients().size());

        // Testujemy wyrejestrowanie null'a
        assertThrows(MainException.class, () -> cm.unregisterClient(null));
        assertEquals(2, cm.getAllClients().size());
    }

    @Test
    public void testGetCourtByPersonalId() {
        ClientManager cm = new ClientManager();
        assertNotNull(cm);

        Client testClient1 =
                cm.registerClient("Adam", "Smith", "12345678901", testClientType);
        assertNotNull(testClient1);
        assertEquals(1, cm.getAllClients().size());
        Client testClient2 =
                cm.registerClient("Eva", "Brown", "12345678902", testClientType);
        assertNotNull(testClient2);
        assertEquals(2, cm.getAllClients().size());

        Client newClient = cm.getClientByPersonalId("12345678901");
        assertNotNull(newClient);
        assertEquals(testClient1, newClient);
        Client newClient2 = cm.getClientByPersonalId("12345678999");
        assertNull(newClient2);
    }
}
