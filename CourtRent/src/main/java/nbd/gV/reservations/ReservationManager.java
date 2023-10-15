package nbd.gV.reservations;

import nbd.gV.Repository;
import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.Coach;
import nbd.gV.clients.Normal;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class ReservationManager {
    private Repository<Reservation> currentReservations;
    private Repository<Reservation> archiveReservations;

    public ReservationManager(Repository<Reservation> currentReservations, Repository<Reservation> archiveReservations) {
        this.currentReservations = currentReservations;
        this.archiveReservations = archiveReservations;
    }

    public ReservationManager() {
        this(new Repository<>(), new Repository<>());
    }

    public Reservation makeReservation(Client client, Court court, LocalDateTime beginTime) {
        if (client == null || court == null) {
            throw new MainException("Jeden z podanych parametrow [client/court] prowadzi do nieistniejacego obiektu!");
        }

        ///TODO przy bazie danych warunek do wywalenia
        if (!court.isRented() && !client.isArchive() && !court.isArchive()) {
            Reservation newReservation = new Reservation(UUID.randomUUID(), client, court, beginTime);
            currentReservations.add(newReservation);
            return newReservation;
        } else if (client.isArchive()) {
            throw new ClientException("Nie udalo sie utworzyc rezerwacji - klient jest archiwalny!");
        } else if (court.isArchive()) {
            throw new CourtException("Nie udalo sie utworzyc rezerwacji - boisko jest archiwalne!");
        }
        else {
            throw new ReservationException("To boisko jest aktualnie wypozyczone!");
        }
    }

    public Reservation makeReservation(Client client, Court court) {
        return makeReservation(client, court, null);
    }

    public void returnCourt(Court court, LocalDateTime endTime) {
        if (court == null) {
            throw new MainException("Nie mozna zwrocic nieistniejacego boiska!");
        } else if (!court.isRented()) {
            throw new CourtException("Nie mozna zwrocic niewypozyczonego boiska!");
        } else {
            Reservation reservation = getCourtReservation(court);
            reservation.endReservation(endTime);
            archiveReservations.add(reservation);
            currentReservations.remove(reservation);
        }
    }

    public void returnCourt(Court court) {
        returnCourt(court, null);
    }

    public List<Reservation> getAllClientReservations(Client client) {
        if (client == null) {
            throw new MainException("Nie istniejacy klient nie moze posiadac rezerwacji!");
        }
        return currentReservations.find((r) -> r.getClient() == client);
    }

    public Reservation getCourtReservation(Court court) {
        if (court == null) {
            throw new MainException("Nie istniejace boisko nie moze posiadac rezerwacji!");
        }
        try {
            return currentReservations.find((r) -> r.getCourt() == court).get(0);
        } catch (IndexOutOfBoundsException ex) {
            throw new CourtException("To boisko nie jest aktualnie zarezerwowane!");
        }
    }

    public double checkClientReservationBalance(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna obliczyc salda dla nieistniejacego klienta!");
        }
        double sum = 0;
        for (var reservation : archiveReservations.find((r) -> r.getClient() == client)) {
            sum += reservation.getReservationCost();
        }
        return sum;
    }

    public void changeClientType(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna zmienic typu nieistniejacego klienta!");
        }
        double balance = checkClientReservationBalance(client);
        if (balance > 10000) {
            client.setClientType(new Coach());
        } else if (balance > 3000) {
            client.setClientType(new Athlete());
        }
    }

    public List<Reservation> findReservations(Predicate<Reservation> reservationPredicate,
                                              boolean searchArchiveReservation) {
        if (searchArchiveReservation) {
            return archiveReservations.find(reservationPredicate);
        } else {
            return currentReservations.find(reservationPredicate);
        }
    }

    public List<Reservation> findReservations(Predicate<Reservation> reservationPredicate) {
        return findReservations(reservationPredicate, false);
    }

    public List<Reservation> getAllCurrentReservations() {
        return currentReservations.find((r) -> true);
    }

    public List<Reservation> getAllArchiveReservations() {
        return archiveReservations.find((r) -> true);
    }
}
