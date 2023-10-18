package nbd.gV.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.JakartaException;

import java.util.List;
import java.util.UUID;

public class ClientRepository extends Repository<Client> {

    public ClientRepository(String unitName) {
        super(unitName);
    }

    @Override
    public Client findByUUID(UUID identifier) {
        Client returnClient = null;
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
        List<Client> clientList = null;
        try{
            getEntityManager().getTransaction().begin();
            CriteriaQuery<Client> findAllClients = getEntityManager().getCriteriaBuilder().createQuery(Client.class);
            Root<Client> clientRoot = findAllClients.from(Client.class);
            findAllClients.select(clientRoot);
            clientList = getEntityManager().createQuery(findAllClients).setLockMode(LockModeType.PESSIMISTIC_READ)
                    .getResultList();
            getEntityManager().getTransaction().commit();
        } catch (IllegalStateException | IllegalArgumentException exception){
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return clientList;
    }

//    @Override
//    public List<Client> find(CriteriaQuery<Client> query) {
//        List<Client> returnList;
//        try {
//            getEntityManager().getTransaction().begin();
//            returnList = getEntityManager().createQuery(query).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
//            getEntityManager().getTransaction().commit();
//        } catch (IllegalStateException | IllegalArgumentException exception){
//            getEntityManager().getTransaction().rollback();
//            throw new JakartaException(exception.getMessage());
//        }
//        return returnList;
//    }
}
