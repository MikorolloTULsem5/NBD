package repositoryTests;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

import nbd.gV.SchemaConst;
import nbd.gV.clients.Client;
import nbd.gV.repositories.clients.ClientCassandraRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import java.util.Comparator;
import java.util.UUID;

import static nbd.gV.SchemaConst.CLIENTS_TABLE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientCassandraRepositoryTest {

    static ClientCassandraRepository clientRepository;
    static CqlSession session;

    Client client1;
    Client client2;
    Client client3;
    final String testClientType = "normal";

    @BeforeAll
    static void setupTestSession() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandranode1", 9042))
                .addContactPoint(new InetSocketAddress("cassandranode2", 9043))
                .addContactPoint(new InetSocketAddress("cassandranode3", 9044))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("admin", "adminpassword")
                .withKeyspace(CqlIdentifier.fromCql(SchemaConst.RESERVE_A_COURT_NAMESPACE))
                .build();

        session.execute("DROP TABLE IF EXISTS " + CLIENTS_TABLE);

        clientRepository = new ClientCassandraRepository();
    }

    @AfterAll
    static void cleanTable() {
        session.execute("TRUNCATE " + CLIENTS_TABLE);
    }

    @BeforeEach
    void initData() {
        cleanTable();

        client1 = new Client("Adam", "Smith", "12345678901", testClientType);
        client2 = new Client("Eva", "Smith", "12345678902", testClientType);
        client3 = new Client("John", "Lenon", "12345678903", testClientType);
    }

    @Test
    void testCreatingRepository() {
        assertNotNull(clientRepository);
        assertTrue(session.execute("SELECT * FROM " + CLIENTS_TABLE).all().isEmpty());
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        assertEquals(1, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client2);
        assertEquals(2, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        assertEquals(1, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        assertEquals(1, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
    }

    @Test
    void testFindingDocumentsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        assertEquals(client1, clientRepository.read("12345678901"));
        assertEquals(client3, clientRepository.read("12345678903"));
    }

    @Test
    void testFindingDocumentsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        assertEquals(1, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        assertNull(clientRepository.read("12345678904"));
    }

    @Test
    void testFindingAllDocumentsInDB() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        var clientsList = clientRepository.readAll();
        clientsList.sort(Comparator.comparing(Client::getFirstName));
        assertEquals(3, clientsList.size());

        assertEquals(client1, clientsList.get(0));
        assertEquals(client2, clientsList.get(1));
        assertEquals(client3, clientsList.get(2));
    }

    @Test
    void testFindingByUUID() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        assertEquals(client1, clientRepository.readByUUID(client1.getClientId()));
        assertEquals(client3, clientRepository.readByUUID(client3.getClientId()));
    }

    @Test
    void testFindingByUUIDNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        assertEquals(1, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        assertNull(clientRepository.readByUUID(UUID.randomUUID()));
    }

    @Test
    void testDeletingDocumentsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        clientRepository.delete(client1);
        assertEquals(2, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        //Check the rest
        var clientsList = clientRepository.readAll();
        clientsList.sort(Comparator.comparing(Client::getFirstName));
        assertEquals(2, clientsList.size());

        assertEquals(client2, clientsList.get(0));
        assertEquals(client3, clientsList.get(1));
    }

    @Test
    void testDeletingDocumentsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        clientRepository.delete(client1);
        assertEquals(2, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        clientRepository.delete(client1);
        assertEquals(2, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
    }

    @Test
    void testUpdatingDocumentsInDBPositive() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        Client clientToUpd = clientRepository.readByUUID(client1.getClientId());
        assertEquals("Adam", clientToUpd.getFirstName());

        clientToUpd.setFirstName("Jacob");

        clientRepository.update(clientToUpd);
        assertEquals("Jacob", clientRepository.readByUUID(client1.getClientId()).getFirstName());
    }

    @Test
    void testUpdatingDocumentsInDBNegative() {
        assertEquals(0, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());
        clientRepository.create(client1);
        clientRepository.create(client2);
        clientRepository.create(client3);
        assertEquals(3, session.execute("SELECT * FROM " + CLIENTS_TABLE).all().size());

        Client newClient = new Client("Jacob", "Long", "12345678999", "normal");
        clientRepository.update(newClient);

        var clientsList = clientRepository.readAll();
        clientsList.sort(Comparator.comparing(Client::getFirstName));
        assertEquals(4, clientsList.size());

        assertEquals(newClient, clientsList.get(2));
    }
}
