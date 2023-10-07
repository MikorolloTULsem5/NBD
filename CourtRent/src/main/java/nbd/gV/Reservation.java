package nbd.gV;

import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.ReservationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Formatter;

public class Reservation {
    private final int id;
    private Client client;
    private Court court;
    private LocalDateTime beginTime;
    private LocalDateTime endTime = null;
    private double reservationCost;

    public Reservation(int id, Client client, Court court, LocalDateTime beginTime) {
        if (id <= 0 || client == null || court == null)
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu rezerwacji!");

        this.id = id;
        this.client = client;
        this.court = court;

        if (beginTime == null) {
            this.beginTime = LocalDateTime.now();
        } else {
            this.beginTime = beginTime;
        }
    }

    public int getId() {
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

        long duration = Duration.between(beginTime, endTime).getSeconds();
        int hoursDur = (int) (duration / 3600);
        int minutesDur = (int) ((duration / 60) % 60);

        if (endTime != null && !(hoursDur == 0 && minutesDur == 0)) {
            hours = (minutesDur == 0) ? hoursDur : (hoursDur + 1);
        }

        return hours;
    }

    public void endReservation(LocalDateTime endingDate) {
        if (endTime == null) {
            endTime = (endingDate == null) ? LocalDateTime.now() : endingDate;
            if (Duration.between(beginTime, endTime).isNegative()) {
                endTime = beginTime;
            }

            client.setArchive(true);
            court.setArchive(true);

            if (getReservationHours() <= client.getClientMaxHours()) {
                reservationCost = getReservationHours() * court.getActualReservationPrice() -
                        client.applyDiscount(getReservationHours() * court.getActualReservationPrice());
            } else {
                reservationCost = court.getActualReservationPrice() *
                        (client.getClientMaxHours() + (getReservationHours() - client.getClientMaxHours()) * 1.5) -
                        client.applyDiscount(client.getClientMaxHours() * court.getActualReservationPrice());
            }
        } else {
            throw new ReservationException("Ta rezerwacja juz sie zakonczyla i nie mozna zmienic jej daty!");
        }
    }

    public String getReservationInfo() {
        return "Rezerwacja nr %d przez '%s' boiska: '%s', od godziny [%s]%n".formatted(id,
                client.getClientInfo().replace("\n", ""),
                court.getCourtInfo().replace("\n", ""),
                beginTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
    }
}
