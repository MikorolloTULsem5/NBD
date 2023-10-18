package nbd.gV.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.JakartaException;

import java.util.List;
import java.util.UUID;

public class CourtRepository extends Repository<Court> {

    public CourtRepository(String unitName) {
        super(unitName);
    }

    @Override
    public Court findByUUID(UUID identifier) {
        Court returnCourt;
        try {
            getEntityManager().getTransaction().begin();
            returnCourt = getEntityManager().find(Court.class, identifier, LockModeType.PESSIMISTIC_READ);
            getEntityManager().getTransaction().commit();
        } catch (IllegalArgumentException | TransactionRequiredException | PessimisticLockException
                 | LockTimeoutException exception) {
            getEntityManager().getTransaction().rollback();
            throw new JakartaException(exception.getMessage());
        }
        return returnCourt;
    }

    @Override
    public List<Court> findAll() {
        CriteriaQuery<Court> findAllCourts = getEntityManager().getCriteriaBuilder().createQuery(Court.class);
        Root<Court> courtRoot = findAllCourts.from(Court.class);
        findAllCourts.select(courtRoot);
        return find(findAllCourts);
    }

//    @Override
//    public List<Court> find(CriteriaQuery<Court> query) {
//        List<Court> returnList;
//        try {
//            getEntityManager().getTransaction().begin();
//            returnList = getEntityManager().createQuery(query).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
//            getEntityManager().getTransaction().commit();
//        } catch (IllegalStateException | IllegalArgumentException exception){
//            getEntityManager().getTransaction().rollback();
//            throw new JakartaException(exception.getMessage());
//        }
//        return returnList;
//    }
}
