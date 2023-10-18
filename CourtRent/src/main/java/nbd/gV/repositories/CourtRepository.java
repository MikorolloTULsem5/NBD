package nbd.gV.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.JakartaException;

import java.util.List;
import java.util.UUID;

public class CourtRepository extends Repository<Court>{

    public CourtRepository(String unitName) {
        super(unitName);
    }

    @Override
    public Court findByUUID(UUID identifier) {
        Court returnCourt = null;
        try{
            getEntityManager().getTransaction().begin();
            returnCourt = getEntityManager().find(Court.class, identifier, LockModeType.PESSIMISTIC_READ);
            getEntityManager().getTransaction().commit();
        }catch (IllegalArgumentException | TransactionRequiredException | OptimisticLockException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return returnCourt;
    }

    @Override
    public List<Court> findAll() {
        List<Court> courtList = null;
        try{
            getEntityManager().getTransaction().begin();
            CriteriaQuery<Court> findAllCourts = getEntityManager().getCriteriaBuilder().createQuery(Court.class);
            Root<Court> screeningRoomRoot = findAllCourts.from(Court.class);
            findAllCourts.select(screeningRoomRoot);
            courtList = getEntityManager().createQuery(findAllCourts).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
            getEntityManager().getTransaction().commit();
        } catch (IllegalStateException | IllegalArgumentException exception){
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return courtList;
    }
}