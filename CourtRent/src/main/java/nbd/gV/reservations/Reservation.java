package nbd.gV.reservations;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@Access(value = AccessType.FIELD)
public class Reservation {
    @Id
    private UUID id;
    @NotEmpty
    @ManyToOne
    @JoinColumn
    private Client client;
    @NotEmpty
    @ManyToOne
    @JoinColumn
    private Court court;
    @NotEmpty
    private LocalDateTime beginTime;
    private LocalDateTime endTime = null;
    private double reservationCost;

    public Reservation(UUID id, Client client, Court court, LocalDateTime beginTime) {
        if (id == null || client == null || court == null)
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu rezerwacji!");

        this.id = id;
        this.client = client;
        this.court = court;
        this.beginTime = (beginTime == null) ? LocalDateTime.now() : beginTime;
        court.setRented(true);
    }

    public Reservation() {
    }

    public UUID getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Court getCourt() {
        return court;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public double getReservationCost() {
        return reservationCost;
    }

    public int getReservationHours() {
        int hours = 0;

        if (endTime != null) {
            long duration = Duration.between(beginTime, endTime).getSeconds();
            int hoursDur = (int) (duration / 3600);
            int minutesDur = (int) ((duration / 60) % 60);

            if (!(hoursDur == 0 && minutesDur == 0)) {
                hours = (minutesDur == 0) ? hoursDur : (hoursDur + 1);
            }
        }

        return hours;
    }

    public void endReservation(LocalDateTime endingDate) {
        if (endTime == null) {
            endTime = (endingDate == null) ? LocalDateTime.now() : endingDate;
            if (Duration.between(beginTime, endTime).isNegative()) {
                endTime = beginTime;
            }

            court.setRented(false);

            if (getReservationHours() <= client.getClientMaxHours()) {
                reservationCost = getReservationHours() * court.getBaseCost() -
                        client.applyDiscount(getReservationHours() * court.getBaseCost());
            } else {
                reservationCost = court.getBaseCost() *
                        (client.getClientMaxHours() + (getReservationHours() - client.getClientMaxHours()) * 1.5) -
                        client.applyDiscount(client.getClientMaxHours() * court.getBaseCost());
            }
        } else {
            throw new ReservationException("Ta rezerwacja juz sie zakonczyla i nie mozna zmienic jej daty!");
        }
    }

    public String getReservationInfo() {
        return new Formatter(Locale.GERMAN).format("Rezerwacja nr %s przez '%s' boiska: '%s', od godziny [%s]%s%n", id,
                client.getClientInfo().replace("\n", ""),
                court.getCourtInfo().replace("\n", ""),
                beginTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")),
                (endTime == null) ? "." : (" do godziny [%s].".formatted(
                        endTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))))).toString();
    }
}
