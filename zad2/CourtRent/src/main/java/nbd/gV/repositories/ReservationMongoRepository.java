package nbd.gV.repositories;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.reservations.Reservation;

import java.util.ArrayList;
import java.util.UUID;

public class ReservationMongoRepository extends AbstractMongoRepository<ReservationMapper> {

    //Sprawdzenie spojnosci bazy
    @Override
    public boolean create(ReservationMapper reservationMapper) {
        try {
            //Check client
            var list1 = getDatabase().getCollection("clients", ClientMapper.class)
                    .find(Filters.eq("_id", reservationMapper.getClientId())).into(new ArrayList<>());
            if (list1.isEmpty()) {
                throw new ReservationException("Brak podanego klienta w bazie!");
            }
            Client clientFound = ClientMapper.fromMongoClient(list1.get(0));

            //Check court
            var list2 = getDatabase().getCollection("courts", CourtMapper.class)
                    .find(Filters.eq("_id", reservationMapper.getCourtId())).into(new ArrayList<>());
            if (list2.isEmpty()) {
                throw new ReservationException("Brak podanego boiska w bazie!");
            }
            Court courtFound = CourtMapper.fromMongoCourt(list2.get(0));

            if (!courtFound.isRented() && !clientFound.isArchive() && !courtFound.isArchive()) {
                InsertOneResult result;
                ClientSession clientSession = getMongoClient().startSession();
                try {
                    clientSession.startTransaction();
                    result = this.getCollection().insertOne(ReservationMapper.toMongoReservation(
                            new Reservation(UUID.fromString(reservationMapper.getId()),
                                    clientFound, courtFound, reservationMapper.getBeginTime())));
                    if (result.wasAcknowledged()) {
                        getDatabase().getCollection("courts", CourtMapper.class).updateOne(
//                                clientSession,
                                Filters.eq("_id", courtFound.getCourtId().toString()),
                                Updates.set("rented", 1));
                    }
                } catch (Exception e) {
                    clientSession.abortTransaction();
                    clientSession.close();
                    throw new MyMongoException(e.getMessage());
                } finally {
                    clientSession.close();
                }
                return result.wasAcknowledged();
            } else if (clientFound.isArchive()) {
                throw new ClientException("Nie udalo sie utworzyc rezerwacji - klient jest archiwalny!");
            } else if (courtFound.isArchive()) {
                throw new CourtException("Nie udalo sie utworzyc rezerwacji - boisko jest archiwalne!");
            } else {
                throw new ReservationException("To boisko jest aktualnie wypozyczone!");
            }
        } catch (MongoWriteException | MongoCommandException exception) {
            throw new MyMongoException(exception.getMessage());
        }
    }

//    public void update(Court court, LocalDateTime endTime) {
//        try {
//            getEntityManager().getTransaction().begin();
//            Court court1 = getEntityManager().find(Court.class, court.getCourtId(), LockModeType.PESSIMISTIC_WRITE);
//            if (court1.isRented()) {
//                court.setRented(false);
//                getEntityManager().merge(court);
//                CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
//                CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
//                Root<Reservation> reservationRoot = query.from(Reservation.class);
//                query.select(reservationRoot).where(cb.and(cb.equal(reservationRoot.get(Reservation_.COURT), court), cb.isNull(reservationRoot.get(Reservation_.END_TIME))));
//                Reservation reservation = getEntityManager().createQuery(query).setLockMode(LockModeType.PESSIMISTIC_READ).getSingleResult();
//                reservation.endReservation(endTime);
//                getEntityManager().merge(reservation);
//                getEntityManager().getTransaction().commit();
//            } else {
//                getEntityManager().getTransaction().rollback();
//                throw new ReservationException("To boisko nie jest aktualnie wypozyczone!");
//            }
//        } catch (IllegalArgumentException | TransactionRequiredException | PessimisticLockException exception) {
//            getEntityManager().getTransaction().rollback();
//            throw new JakartaException(exception.getMessage());
//        }
//    }

    @Override
    protected MongoCollection<ReservationMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), ReservationMapper.class);
    }

    @Override
    public String getCollectionName() {
        return "reservations";
    }
}
