package nbd.gV.reservations;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MainException;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.repositories.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservationManager {
    private final ReservationRepository reservationRepository;

    public ReservationManager(String unitName) {
        reservationRepository = new ReservationRepository(unitName);
    }

    public ReservationManager() {
        reservationRepository = new ReservationRepository("default");
    }

    //Rezerwacji mozna dokonac tylko obiektami ktore juz znajduja sie w bazie danych
    public Reservation makeReservation(Client client, Court court, LocalDateTime beginTime) {
        if (client == null || court == null) {
            throw new MainException("Jeden z podanych parametrow [client/court] prowadzi do nieistniejacego obiektu!");
        }
        try {
            return reservationRepository.create(client,court,beginTime);
        } catch (JakartaException exception) {
            throw new ReservationException("Blad transakcji.");
        }
    }

    public Reservation makeReservation(Client client, Court court) {
        return makeReservation(client, court, LocalDateTime.now());
    }

    public void returnCourt(Court court, LocalDateTime endTime) {
        if (court == null) {
            throw new MainException("Nie mozna zwrocic nieistniejacego boiska!");
        } else if (!court.isRented()) {
            throw new CourtException("Nie mozna zwrocic niewypozyczonego boiska!");
        } else {
            try {
                reservationRepository.update(court,endTime);
            } catch (JakartaException exception) {
                throw new ReservationException("Blad transakcji.");
            }
        }
    }

    public void returnCourt(Court court) {
        returnCourt(court, LocalDateTime.now());
    }

    public List<Reservation> getAllClientReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
        Root<Reservation> reservationRoot = query.from(Reservation.class);
        query.select(reservationRoot).where(cb.equal(reservationRoot.get(Reservation_.CLIENT), client));
        List<Reservation> result = reservationRepository.find(query);
        return result.isEmpty() ? null : result;
    }

    public List<Reservation> getClientEndedReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
        Root<Reservation> reservationRoot = query.from(Reservation.class);
        query.select(reservationRoot).where(cb.and(cb.equal(reservationRoot.get(Reservation_.CLIENT), client), cb.isNotNull(reservationRoot.get(Reservation_.END_TIME))));
        return reservationRepository.find(query);
    }

    public Reservation getCourtReservation(Court court) {
        if (court == null) {
            throw new MainException("Nie istniejace boisko nie moze posiadac rezerwacji!");
        }
        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
        Root<Reservation> reservationRoot = query.from(Reservation.class);
        query.select(reservationRoot).where(cb.equal(reservationRoot.get(Reservation_.COURT), court));
        List<Reservation> result = reservationRepository.find(query);
        return result.isEmpty() ? null : result.get(0);
    }

    public double checkClientReservationBalance(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna obliczyc salda dla nieistniejacego klienta!");
        }
        double sum = 0;
        List<Reservation> reservationList = getClientEndedReservations(client);
        for (Reservation reservation : reservationList) {
            sum += reservation.getReservationCost();
        }
        return sum;
    }

    public List<Reservation> getAllCurrentReservations() {
        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
        Root<Reservation> reservationRoot = query.from(Reservation.class);
        query.select(reservationRoot).where(cb.isNull(reservationRoot.get(Reservation_.END_TIME)));
        return reservationRepository.find(query);
    }

    public List<Reservation> getAllArchiveReservations() {
        CriteriaBuilder cb = reservationRepository.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
        Root<Reservation> reservationRoot = query.from(Reservation.class);
        query.select(reservationRoot).where(cb.isNotNull(reservationRoot.get(Reservation_.END_TIME)));
        return reservationRepository.find(query);
    }

    public Reservation getReservationByID(UUID uuid){
        return reservationRepository.findByUUID(uuid);
    }

}
