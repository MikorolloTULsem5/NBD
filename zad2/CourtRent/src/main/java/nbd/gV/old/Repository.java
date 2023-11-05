package nbd.gV.old;

import jakarta.persistence.LockModeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import nbd.gV.exceptions.JakartaException;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.UUID;

public abstract class Repository<T> {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public Repository(String unitName) {
        entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void create(T element) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(element);
            entityManager.getTransaction().commit();
        } catch (PersistenceException | IllegalArgumentException | IllegalStateException exception) {
            entityManager.getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }

    public void delete(T element) {
        try {
            entityManager.getTransaction().begin();
            entityManager.lock(element, LockModeType.PESSIMISTIC_WRITE);
            entityManager.remove(element);
            entityManager.getTransaction().commit();
        } catch (PersistenceException | IllegalArgumentException | IllegalStateException exception) {
            entityManager.getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }

    public void update(T element) {
        try {
            entityManager.getTransaction().begin();
            entityManager.lock(element, LockModeType.PESSIMISTIC_WRITE);
            entityManager.merge(element);
            entityManager.getTransaction().commit();
        } catch (PersistenceException | IllegalArgumentException | IllegalStateException exception) {
            entityManager.getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public abstract T findByUUID(UUID identifier);

    public abstract List<T> findAll();

    public List<T> find(CriteriaQuery<T> query) {
        List<T> returnList;
        try {
            getEntityManager().getTransaction().begin();
            returnList = getEntityManager().createQuery(query).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
            getEntityManager().getTransaction().commit();
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw new JakartaException(exception.getMessage());
        }
        return returnList;
    }
}