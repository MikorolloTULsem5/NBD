package nbd.gV;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;

import java.util.UUID;

import static nbd.gV.reservations.Reservation_.court;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Client client = new Client("imie","nazwisko","123", new Athlete());
        UUID uuid = client.getClientID();
        entityManager.getTransaction().begin();
        entityManager.persist(client);
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        Client client2= entityManager.find(Client.class, uuid, LockModeType.PESSIMISTIC_READ);
        entityManager.getTransaction().commit();
        System.out.println(client2.getClientInfo());
    }
}