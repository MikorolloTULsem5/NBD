package nbd.gV.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.Reservation_;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservationRepository extends Repository<Reservation> {
    public ReservationRepository(String unitName) {
        super(unitName);
    }

    //Sprawdzenie spojnosci bazy
    public Reservation create(Client client, Court court, LocalDateTime beginTime) {
        try {
            getEntityManager().getTransaction().begin();
            Client client1 = getEntityManager().find(Client.class, client.getClientID(), LockModeType.PESSIMISTIC_READ);
            Court court1 = getEntityManager().find(Court.class, court.getCourtId(), LockModeType.PESSIMISTIC_WRITE);
            if (!court1.isRented() && !client1.isArchive() && !court1.isArchive()) {
                court.setRented(true);
                getEntityManager().merge(court);
                Reservation newReservation = new Reservation(UUID.randomUUID(), client, court, beginTime);
                getEntityManager().persist(newReservation);
                getEntityManager().getTransaction().commit();
                return newReservation;
            } else if (client1.isArchive()) {
                getEntityManager().getTransaction().rollback();
                throw new ClientException("Nie udalo sie utworzyc rezerwacji - klient jest archiwalny!");
            } else if (court1.isArchive()) {
                getEntityManager().getTransaction().rollback();
                throw new CourtException("Nie udalo sie utworzyc rezerwacji - boisko jest archiwalne!");
            } else {
                getEntityManager().getTransaction().rollback();
                throw new ReservationException("To boisko jest aktualnie wypozyczone!");
            }
        } catch (IllegalArgumentException | TransactionRequiredException | PessimisticLockException exception) {
            throw new JakartaException(exception.getMessage());
        }
    }

    public void update(Court court, LocalDateTime endTime) {
        try {
            getEntityManager().getTransaction().begin();
            Court court1 = getEntityManager().find(Court.class, court.getCourtId(), LockModeType.PESSIMISTIC_WRITE);
            if (court1.isRented()) {
                court.setRented(false);
                getEntityManager().merge(court);
                CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
                CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
                Root<Reservation> reservationRoot = query.from(Reservation.class);
                query.select(reservationRoot).where(cb.and(cb.equal(reservationRoot.get(Reservation_.COURT), court), cb.isNull(reservationRoot.get(Reservation_.END_TIME))));
                Reservation reservation = getEntityManager().createQuery(query).setLockMode(LockModeType.PESSIMISTIC_READ).getSingleResult();
                reservation.endReservation(endTime);
                getEntityManager().merge(reservation);
                getEntityManager().getTransaction().commit();
            } else {
                getEntityManager().getTransaction().rollback();
                throw new ReservationException("To boisko nie jest aktualnie wypozyczone!");
            }
        } catch (IllegalArgumentException | TransactionRequiredException | PessimisticLockException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
    }


    @Override
    public Reservation findByUUID(UUID identifier) {
        Reservation returnReservation = null;
        try {
            getEntityManager().getTransaction().begin();
            returnReservation = getEntityManager().find(Reservation.class, identifier, LockModeType.PESSIMISTIC_READ);
            getEntityManager().getTransaction().commit();
        } catch (IllegalArgumentException | TransactionRequiredException | PessimisticLockException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return returnReservation;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservationList = null;
        try {
            getEntityManager().getTransaction().begin();
            CriteriaQuery<Reservation> findAllReservations = getEntityManager().getCriteriaBuilder().createQuery(Reservation.class);
            Root<Reservation> screeningRoomRoot = findAllReservations.from(Reservation.class);
            findAllReservations.select(screeningRoomRoot);
            reservationList = getEntityManager().createQuery(findAllReservations).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
            getEntityManager().getTransaction().commit();
        } catch (IllegalStateException | IllegalArgumentException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return reservationList;
    }

}
