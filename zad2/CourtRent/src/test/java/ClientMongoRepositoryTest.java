import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.ClientMapper;
import nbd.gV.ClientMongoRepository;
import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Normal;
import nbd.gV.exceptions.MyMongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientMongoRepositoryTest {
    final ClientMongoRepository clientRepository = new ClientMongoRepository();
    ClientMapper clientMapper1;
    ClientMapper clientMapper2;
    ClientMapper clientMapper3;
    Client client1;
    Client client2;
    Client client3;
    final ClientType testClientType = new Normal();

    private MongoCollection<ClientMapper> getTestCollection() {
        return clientRepository.getDatabase()
                .getCollection(clientRepository.getCollectionName(), ClientMapper.class);
    }

    @BeforeEach
    void cleanDataBase() {
        getTestCollection().deleteMany(Filters.empty());

        client1 = new Client("Adam", "Smith", "12345678901", testClientType);
        clientMapper1 = ClientMapper.toMongoClient(client1);

        client2 = new Client("Eva", "Smith", "12345678902", testClientType);
        clientMapper2 = ClientMapper.toMongoClient(client2);

        client3 = new Client("John", "Lenon", "12345678903", testClientType);
        clientMapper3 = ClientMapper.toMongoClient(client3);
    }

    @Test
    void testCreatingRepository() {
        ClientMongoRepository clientRepository = new ClientMongoRepository();
        assertNotNull(clientRepository);
    }

    @Test
    void testAddingNewDocumentToDBPositive() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper2));
        assertEquals(2, getTestCollection().find().into(new ArrayList<>()).size());
    }

    @Test
    void testAddingNewDocumentToDBNegative() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
        assertThrows(MyMongoException.class, () -> clientRepository.create(clientMapper1));
        assertEquals(1, getTestCollection().find().into(new ArrayList<>()).size());
    }

    //
//    @Test
//    void testAddingNewRecordToDBWithSameClientType() {
//        CriteriaBuilder cb = clientRepository.getEntityManager().getCriteriaBuilder();
//
//        CriteriaQuery<Long> query = cb.createQuery(Long.class);
//        From<Client, Client> from = query.from(Client.class);
//        query.select(cb.count(from));
//        long count = clientRepository.getEntityManager().createQuery(query).getSingleResult();
//
//        assertEquals(0, count);
//
//        /*--------------------------------------------------------------------------------------------------------*/
//
//        Repository<ClientType> clientTypeRepository = new Repository<>("test") {
//            @Override
//            public ClientType findByUUID(UUID identifier) {
//                return null;
//            }
//
//            @Override
//            public List<ClientType> findAll() {
//                return null;
//            }
//        };
//
//        CriteriaQuery<Long> queryClientType = cb.createQuery(Long.class);
//        From<ClientType, ClientType> fromClientType = queryClientType.from(ClientType.class);
//        queryClientType.select(cb.count(fromClientType));
//        long countClientType = clientTypeRepository.getEntityManager().createQuery(queryClientType).getSingleResult();
//
//        assertEquals(0, countClientType);
//
//        /*--------------------------------------------------------------------------------------------------------*/
//        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
//        clientRepository.create(client1);
//        Client client2 = new Client("Adam", "Red", "12345678912", new Coach());
//        clientRepository.create(client2);
//        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
//        clientRepository.create(client3);
//        Client client4 = new Client("Jan", "Smith", "12345678914", new Coach());
//        clientRepository.create(client4);
//
//        count = clientRepository.getEntityManager().createQuery(query).getSingleResult();
//        assertEquals(4, count);
//
//        countClientType = clientTypeRepository.getEntityManager().createQuery(queryClientType).getSingleResult();
//        assertEquals(2, countClientType);
//    }
//
    @Test
    void testFindingDocumentInDB() {
        assertEquals(0, getTestCollection().find().into(new ArrayList<>()).size());
        assertTrue(clientRepository.create(clientMapper1));
        assertTrue(clientRepository.create(clientMapper2));
        assertTrue(clientRepository.create(clientMapper3));
        assertEquals(3, getTestCollection().find().into(new ArrayList<>()).size());

        var clientsList1 = clientRepository.read(Filters.eq("firstname", "John"));
        assertEquals(1, clientsList1.size());
        assertEquals(clientMapper3, clientsList1.get(0));
    }
//
//    @Test
//    void testFindingByUUID() {
//        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
//        clientRepository.create(client1);
//        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
//        clientRepository.create(client2);
//        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
//
//        assertEquals(client1, clientRepository.findByUUID(client1.getClientID()));
//        assertEquals(client2, clientRepository.findByUUID(client2.getClientID()));
//
//        assertNull(clientRepository.findByUUID(client3.getClientID()));
//        assertThrows(JakartaException.class, () -> clientRepository.findByUUID(null));
//    }
//
//    @Test
//    void testDeletingRecordsInDB() {
//        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
//        clientRepository.create(client1);
//        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
//        clientRepository.create(client2);
//        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
//        clientRepository.create(client3);
//        Client client4 = new Client("Jan", "Smith", "12345678914", new Normal());
//        clientRepository.create(client4);
//
//        assertEquals(4, clientRepository.findAll().size());
//        clientRepository.delete(client2);
//        assertEquals(3, clientRepository.findAll().size());
//
//        clientRepository.delete(client4);
//        var courts = clientRepository.findAll();
//        assertEquals(2, courts.size());
//        assertEquals(client1, courts.get(0));
//        assertEquals(client3, courts.get(1));
//
//        assertThrows(JakartaException.class, () -> clientRepository.delete(null));
//        assertThrows(JakartaException.class, () -> clientRepository.delete(client4));
//        assertEquals(2, clientRepository.findAll().size());
//    }
//
//    @Test
//    void testUpdatingRecordsInDB() {
//        Client client1 = new Client("John", "Smith", "12345678911", new Normal());
//        clientRepository.create(client1);
//        Client client2 = new Client("Adam", "Red", "12345678912", new Normal());
//        clientRepository.create(client2);
//        Client client3 = new Client("Adam", "Black", "12345678913", new Normal());
//        assertEquals(2, clientRepository.findAll().size());
//
//        assertEquals("Red", clientRepository.findByUUID(client2.getClientID()).getLastName());
//        client2.setLastName("Purple");
//        clientRepository.update(client2);
//        assertEquals("Purple", clientRepository.findByUUID(client2.getClientID()).getLastName());
//
//        assertThrows(JakartaException.class, () -> clientRepository.update(client3));
//        assertThrows(JakartaException.class, () -> clientRepository.update(null));
//    }
}
