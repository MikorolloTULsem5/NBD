package nbd.gV;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.Normal;
import nbd.gV.clients.ClientManager;
import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;
import nbd.gV.exceptions.CourtException;
import nbd.gV.repositories.CourtRepository;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        CourtManager courtManager = new CourtManager("test");
        Court testCourt3 = new Court(41,11,3);
        courtManager.unregisterCourt(testCourt3);
    }
}