package nbd.gV.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Client_;
import nbd.gV.exceptions.JakartaException;

import java.util.List;
import java.util.UUID;

public class ClientRepository extends Repository<Client> {

    public ClientRepository(String unitName) {
        super(unitName);
    }

    private ClientType returnClientTypeFromDB(ClientType clientType) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Client> query = cb.createQuery(Client.class);
        Root<Client> clientRoot = query.from(Client.class);
        query.select(clientRoot).where(cb.equal(clientRoot.get(Client_.CLIENT_TYPE), clientType));
        List<Client> result = find(query);

        return result.isEmpty() ? null : result.get(0).getClientType();
    }

    @Override
    public void create(Client newClient) {
        EntityManager entityManager = getEntityManager();
        try {
            ClientType clientTypeFromDB = returnClientTypeFromDB(newClient.getClientType());

            entityManager.getTransaction().begin();
            if (clientTypeFromDB != null) {
                newClient.setClientType(clientTypeFromDB);
            }
            entityManager.persist(newClient);
            entityManager.getTransaction().commit();
        } catch (PersistenceException | IllegalArgumentException | IllegalStateException
                 | NullPointerException exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new JakartaException(exception.getMessage());
        }
    }

    @Override
    public Client findByUUID(UUID identifier) {
        Client returnClient;
        try{
            getEntityManager().getTransaction().begin();
            returnClient = getEntityManager().find(Client.class, identifier, LockModeType.PESSIMISTIC_READ);
            getEntityManager().getTransaction().commit();
        }catch (IllegalArgumentException | TransactionRequiredException | PessimisticLockException
                | LockTimeoutException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return returnClient;
    }

    @Override
    public List<Client> findAll() {
        CriteriaQuery<Client> findAllClients = getEntityManager().getCriteriaBuilder().createQuery(Client.class);
        Root<Client> courtClient = findAllClients.from(Client.class);
        findAllClients.select(courtClient);
        return find(findAllClients);
    }
}
