package nbd.gV.repositories;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.JakartaException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.exceptions.ReservationException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.mappers.CourtMapper;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.reservations.Reservation;

import java.time.LocalDateTime;
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
                    result = this.getCollection().insertOne(clientSession, ReservationMapper.toMongoReservation(
                            new Reservation(UUID.fromString(reservationMapper.getId()),
                                    clientFound, courtFound, reservationMapper.getBeginTime())));
                    if (result.wasAcknowledged()) {
                        getDatabase().getCollection("courts", CourtMapper.class).updateOne(
                                clientSession,
                                Filters.eq("_id", courtFound.getCourtId().toString()),
                                Updates.inc("rented", 1));
                    }
                    clientSession.commitTransaction();
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

    public void update(Court court, LocalDateTime endTime) {
        var listCourt = getDatabase().getCollection("courts", CourtMapper.class)
                .find(Filters.eq("_id", court.getCourtId())).into(new ArrayList<>());
        if (listCourt.isEmpty()) {
            throw new ReservationException("Brak podanego boiska w bazie!");
        }
        if (listCourt.get(0).isRented() == 0) {
            throw new ReservationException("To boisko nie jest aktualnie wypozyczone!");
        }

        var listReservation = getDatabase().getCollection("courts",
                ReservationMapper.class).find(Filters.eq("courtid", court.getCourtId())).into(new ArrayList<>());
        if (listReservation.isEmpty()) {
            throw new ReservationException("Brak rezerwacji, dla podanego boiska, w bazie!");
        }
        Reservation reservationFound = ReservationMapper.fromMongoReservation(listReservation.get(0),
                new ClientMapper(listReservation.get(0).getClientId(), null, null, null,
                        false, null), listCourt.get(0));

        ClientSession clientSession = getMongoClient().startSession();
        court.setRented(false);
        try {
            clientSession.startTransaction();
            reservationFound.endReservation(endTime);

            //Update reservations properties
            update(reservationFound.getId(), "endtime", reservationFound.getEndTime());
            update(reservationFound.getId(), "reservationcost", reservationFound.getReservationCost());

            //Update court's "rented" field
            getDatabase().getCollection("courts", CourtMapper.class).updateOne(
                    clientSession,
                    Filters.eq("_id", listCourt.get(0).getCourtId().toString()),
                    Updates.inc("rented", -1));

            clientSession.commitTransaction();
        } catch (Exception exception) {
            clientSession.abortTransaction();
            clientSession.close();
            court.setRented(true);
            throw new MyMongoException(exception.getMessage());
        } finally {
            clientSession.close();
        }
    }

    @Override
    protected MongoCollection<ReservationMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), ReservationMapper.class);
    }

    @Override
    public String getCollectionName() {
        return "reservations";
    }
}
