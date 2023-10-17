package nbd.gV;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.Normal;
import nbd.gV.clients.ClientManager;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Client client = new Client("imie","nazwisko","123", new Athlete());
        UUID uuid = client.getClientID();
        entityManager.getTransaction().begin();
        entityManager.persist(client);
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        Client client2= entityManager.find(Client.class, uuid, LockModeType.PESSIMISTIC_READ);
        entityManager.getTransaction().commit();
        Client client3 = new Client("imi123e","nazw123isko","1214233", new Athlete());
        UUID uuid2 = client.getClientID();
        entityManager.getTransaction().begin();
        entityManager.persist(client3);
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        Client client4= entityManager.find(Client.class, uuid2, LockModeType.PESSIMISTIC_READ);
        entityManager.getTransaction().commit();
        System.out.println(client2.getClientInfo());
    }
}