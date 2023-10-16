package nbd.gV.courts;

import nbd.gV.Repository;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;

import java.util.List;
import java.util.function.Predicate;

public class CourtManager {

    private Repository<Court> courts;

    public CourtManager(Repository<Court> courts) {
        this.courts = courts;
    }

    public CourtManager() {
        this(new Repository<>());
    }

    public Court registerCourt(double area, int baseCost, int courtNumber) {
        if (courts.findByUID((c) -> c.getCourtNumber() == courtNumber) == null) {
            Court newCourt = new Court(area, baseCost, courtNumber);
            courts.add(newCourt);
            return newCourt;
        } else {
            throw new CourtException("Boisko o tym numerze juz istnieje w repozytorium!");
        }
    }

    public void unregisterCourt(Court court) {
        if (court == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego boiska!");
        }
        if (courts.findByUID((c) -> c.getCourtNumber() == court.getCourtNumber()) != null) {
            if (courts.remove(court)) {
                court.setArchive(true);
            }
        } else {
            throw new CourtException("Podane do wyrejestrowania boisko, nie znajduje sie w repozytorium!");
        }
    }

    public Court getCourt(int courtNumber) {
        if (courtNumber <= 0) {
            throw new MainException("Podano niewlasciwy numer boiska - wartosc niedodatnia");
        }
        return courts.findByUID((c) -> c.getCourtNumber() == courtNumber);
    }

    public List<Court> findCourts(Predicate<Court> predicate) {
        return courts.find(predicate);
    }

    public List<Court> getAllCourts() {
        return courts.find((c) -> true);
    }
}
