package nbd.gV.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.reservations.Reservation;

import java.util.List;
import java.util.UUID;

public class ReservationRepository extends Repository<Reservation> {
    public ReservationRepository(String unitName) {
        super(unitName);
    }

    @Override
    public Reservation findByUUID(UUID identifier) {
        Reservation returnReservation = null;
        try{
            getEntityManager().getTransaction().begin();
            returnReservation = getEntityManager().find(Reservation.class, identifier, LockModeType.PESSIMISTIC_READ);
            getEntityManager().getTransaction().commit();
        }catch (IllegalArgumentException | TransactionRequiredException | OptimisticLockException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return returnReservation;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservationList = null;
        try{
            getEntityManager().getTransaction().begin();
            CriteriaQuery<Reservation> findAllReservations = getEntityManager().getCriteriaBuilder().createQuery(Reservation.class);
            Root<Reservation> screeningRoomRoot = findAllReservations.from(Reservation.class);
            findAllReservations.select(screeningRoomRoot);
            reservationList = getEntityManager().createQuery(findAllReservations).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
            getEntityManager().getTransaction().commit();
        } catch (IllegalStateException | IllegalArgumentException exception){
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return reservationList;
    }
}
