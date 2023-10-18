package nbd.gV.clients;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import nbd.gV.exceptions.JakartaException;

public abstract class Repository<T> {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    Repository(String unitName) {
        entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void add(T element) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(element);
            entityManager.getTransaction().commit();
        } catch (PersistenceException | IllegalStateException exception) {
            entityManager.getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }

    public void remove(T element) {
        try {
            entityManager.getTransaction().begin();
            entityManager.lock(element, LockModeType.PESSIMISTIC_WRITE);
            entityManager.remove(element);
            entityManager.getTransaction().commit();
        } catch (PersistenceException | IllegalStateException exception) {
            entityManager.getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }
}