import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Root;
import nbd.gV.clients.Client;
import nbd.gV.clients.Client_;
import nbd.gV.clients.Normal;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.repositories.ClientRepository;
import nbd.gV.repositories.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientRepositoryTest {
    private final Repository<Client> clientRepository = new ClientRepository("test");

    @AfterEach
    void cleanDataBase() {
        List<Client> listOfClients = clientRepository.findAll();
        listOfClients.forEach(clientRepository::delete);
    }

    @Test
    void testCreatingRepository() {
        Repository<Client> clientRepository = new ClientRepository("test");
        assertNotNull(clientRepository);
        EntityManager em = clientRepository.getEntityManager();
        assertNotNull(em);
        assertTrue(em.isOpen());
    }

    @Test
    void testAddingNewRecordToDB() {
        CriteriaBuilder cb = clientRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        From<Client, Client> from = query.from(Client.class);
        query.select(cb.count(from));
        long count = clientRepository.getEntityManager().createQuery(query).getSingleResult();

        assertEquals(0, count);
        Client client = new Client("John", "Smith", "12345678911", new Normal());
        clientRepository.create(client);
        count = clientRepository.getEntityManager().createQuery(query).getSingleResult();
        assertEquals(1, count);
        assertThrows(JakartaException.class, () -> clientRepository.create(null));
    }

    @Test
    void testFindingRecordsInDB() {
        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
        clientRepository.create(client1);
        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
        clientRepository.create(client2);
        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
        clientRepository.create(client3);
        Client client4 = new Client("Jan", "Smith", "12345678914", new Normal());
        clientRepository.create(client4);

        //Tworzenie zapytania o klientow o imieniu Adam
        CriteriaBuilder cb = clientRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Client> query = cb.createQuery(Client.class);
        Root<Client> clientRoot = query.from(Client.class);
        query.select(clientRoot).where(cb.equal(clientRoot.get(Client_.FIRST_NAME), "Adam"));

        List<Client> resultFirstName = clientRepository.find(query);
        assertEquals(2, resultFirstName.size());
        assertEquals(client2, resultFirstName.get(0));
        assertEquals(client3, resultFirstName.get(1));

        //Tworzenie zapytania o boiska o nazwisku Smith
        query.select(clientRoot).where(cb.equal(clientRoot.get(Client_.LAST_NAME), "Smith"));

        List<Client> resultLastName = clientRepository.find(query);
        assertEquals(2, resultLastName.size());
        assertEquals(client1, resultLastName.get(0));
        assertEquals(client4, resultLastName.get(1));

        //Tworzenie zapytania o klienta o nazwisku Yellow - taki klient nie istnieje
        query.select(clientRoot).where(cb.equal(clientRoot.get(Client_.LAST_NAME), "Yellow"));
        assertTrue(clientRepository.find(query).isEmpty());

        //Pobranie wszystkich boisk z bazy
        List<Client> resultAll = clientRepository.findAll();
        assertEquals(4, resultAll.size());
        assertEquals(client1, resultAll.get(0));
        assertEquals(client2, resultAll.get(1));
        assertEquals(client3, resultAll.get(2));
        assertEquals(client4, resultAll.get(3));
    }

    @Test
    void testFindingByUUID() {
        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
        clientRepository.create(client1);
        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
        clientRepository.create(client2);
        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());

        assertEquals(client1, clientRepository.findByUUID(client1.getClientID()));
        assertEquals(client2, clientRepository.findByUUID(client2.getClientID()));

        assertNull(clientRepository.findByUUID(client3.getClientID()));
        assertThrows(JakartaException.class, () -> clientRepository.findByUUID(null));
    }

    @Test
    void testDeletingRecordsInDB() {
        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
        clientRepository.create(client1);
        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
        clientRepository.create(client2);
        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
        clientRepository.create(client3);
        Client client4 = new Client("Jan", "Smith", "12345678914", new Normal());
        clientRepository.create(client4);

        assertEquals(4, clientRepository.findAll().size());
        clientRepository.delete(client2);
        assertEquals(3, clientRepository.findAll().size());

        clientRepository.delete(client4);
        var courts = clientRepository.findAll();
        assertEquals(2, courts.size());
        assertEquals(client1, courts.get(0));
        assertEquals(client3, courts.get(1));

        assertThrows(JakartaException.class, () -> clientRepository.delete(null));
        assertThrows(JakartaException.class, () -> clientRepository.delete(client4));
        assertEquals(2, clientRepository.findAll().size());
    }

    @Test
    void testUpdatingRecordsInDB() {
        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
        clientRepository.create(client1);
        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
        clientRepository.create(client2);
        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
        assertEquals(2, clientRepository.findAll().size());

        assertEquals("Red", clientRepository.findByUUID(client2.getClientID()).getLastName());
        client2.setLastName("Purple");
        clientRepository.update(client2);
        assertEquals("Purple", clientRepository.findByUUID(client2.getClientID()).getLastName());

        assertThrows(JakartaException.class, () -> clientRepository.update(client3));
        assertThrows(JakartaException.class, () -> clientRepository.update(null));
    }
}
