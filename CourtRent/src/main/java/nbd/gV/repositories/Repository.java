package nbd.gV.repositories;

import jakarta.persistence.*;
import nbd.gV.exceptions.JakartaException;

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
        } catch (PersistenceException | IllegalStateException exception) {
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
        } catch (PersistenceException | IllegalStateException exception) {
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
        } catch (PersistenceException | IllegalStateException exception) {
            entityManager.getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public abstract T findByUUID(UUID identifier);

    public abstract List<T> findAll();
}