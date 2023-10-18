package nbd.gV.courts;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.CourtRepository;

import java.util.List;
import java.util.UUID;

public class CourtManager {

    private final CourtRepository courtRepository;

    public CourtManager(String unitName) {
        courtRepository = new CourtRepository(unitName);
    }

    public CourtManager() {
        courtRepository = new CourtRepository("default");
    }

    public Court registerCourt(double area, int baseCost, int courtNumber) {
        Court court = new Court(area, baseCost, courtNumber);
        try {
            courtRepository.create(court);
        } catch (JakartaException exception) {
            throw new CourtException("Nie udalo sie dodac boiska.");
        }
        return court;
//        if (courts.findByUID((c) -> c.getCourtNumber() == courtNumber) == null) {
//            Court newCourt = new Court(area, baseCost, courtNumber);
//            courts.add(newCourt);
//            return newCourt;
//        } else {
//            throw new CourtException("Boisko o tym numerze juz istnieje w repozytorium!");
//        }
    }

    public void unregisterCourt(Court court) {
        if (court == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego boiska!");
        }
        try {
            court.setArchive(true);
            courtRepository.update(court);
        } catch (JakartaException exception) {
            court.setArchive(false);
            throw new CourtException("Nie udalo sie wyrejestrowac podanego boiska.");
        }
//        if (courts.findByUID((c) -> c.getCourtNumber() == court.getCourtNumber()) != null) {
//            if (courts.remove(court)) {
//                court.setArchive(true);
//            }
//        } else {
//            throw new CourtException("Podane do wyrejestrowania boisko, nie znajduje sie w repozytorium!");
//        }
    }

    public Court getCourt(UUID courtID) {
        try {
            return courtRepository.findByUUID(courtID);
        } catch (JakartaException exception) {
            throw new CourtException("Blad transakcji.");
        }
    }


    public List<Court> getAllCourts() {
        try {
            return courtRepository.findAll();
        } catch (JakartaException exception) {
            throw new CourtException("Nie udalo sie uzyskac boisk.");
        }
    }

    public Court findCourtByCourtNumber(int courtNumber) {
        CriteriaBuilder cb = courtRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Court> query = cb.createQuery(Court.class);
        Root<Court> courtRoot = query.from(Court.class);
        query.select(courtRoot).where(cb.equal(courtRoot.get(Court_.COURT_NUMBER), courtNumber));
        List<Court> result = courtRepository.find(query);
        return result.isEmpty() ? null : result.get(0);
    }
}
