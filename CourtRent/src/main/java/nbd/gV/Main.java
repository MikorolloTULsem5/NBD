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
import nbd.gV.repositories.CourtRepository;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        CourtRepository courtRepository = new CourtRepository("default");
        Court court = new Court(10,12,12);
        courtRepository.create(court);
        courtRepository.create(new Court(9,53,11));
        Court court1 = courtRepository.findByUUID(court.getCourtId());
        System.out.println(court1.getCourtInfo());
        List<Court> courtList = courtRepository.findAll();
        System.out.println(courtList.get(1).getCourtInfo());
        court.setArea(123);
        courtRepository.update(court);
        court1 = courtRepository.findByUUID(court.getCourtId());
        System.out.println(court1.getCourtInfo());
    }
}