package nbd.gV.courts;

import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.repositories.courts.CourtCassandraRepository;

import java.util.List;
import java.util.UUID;

public class CourtManager {

    private final CourtCassandraRepository courtRepository;

    public CourtManager() {
        courtRepository = new CourtCassandraRepository();
    }

    public Court registerCourt(double area, int baseCost, int courtNumber) {
        Court newCourt = new Court(area, baseCost, courtNumber);
        if (courtRepository.read(courtNumber) != null) {
            throw new CourtException("Nie udalo sie zarejestrowac boiska w bazie! - boisko o tym numerze " +
                    "znajduje sie juz w bazie");
        }

        courtRepository.create(newCourt);

        return newCourt;
    }

    public void unregisterCourt(Court court) {
        if (court == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego boiska!");
        }
        try {
            court.setArchive(true);
            courtRepository.update(court);
        } catch (Exception exception) {
            court.setArchive(false);
            throw new CourtException("Nie udalo sie wyrejestrowac podanego boiska. - " + exception.getMessage());
        }
    }

    public Court getCourt(UUID courtID) {
        return courtRepository.readByUUID(courtID);
    }

    public List<Court> getAllCourts() {
        return courtRepository.readAll();
    }

    public Court getCourtByCourtNumber(int courtNumber) {
        return courtRepository.read(courtNumber);
    }
}
