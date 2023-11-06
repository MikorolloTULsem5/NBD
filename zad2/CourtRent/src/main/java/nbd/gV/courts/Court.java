package nbd.gV.courts;

import nbd.gV.exceptions.MainException;

import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Court {
    private UUID courtId;
    private double area;
    private int baseCost;
    private int courtNumber;
    private boolean archive = false;
    private boolean rented = false;

    public Court(double area, int baseCost, int courtNumber) {
        if (area <= 0.0 || baseCost < 0 || courtNumber < 1) {
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu boiska!");
        }
        this.courtId = UUID.randomUUID();
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
    }

    public Court(UUID courtId, double area, int baseCost, int courtNumber) {
        this(area, baseCost, courtNumber);
        this.courtId = courtId;
    }

    public Court() {
    }

    public UUID getCourtId() {
        return courtId;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        if (area > 0.0) {
            this.area = area;
        }
    }

    public int getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(int baseCost) {
        if (baseCost >= 0) {
            this.baseCost = baseCost;
        }
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public String getCourtInfo() {
        return new Formatter(Locale.GERMAN).format("Boisko nr %d o powierzchni %.2f i koszcie za " +
                        "rezerwacje: %.2f PLN\n", getCourtNumber(), getArea(), (double) getBaseCost()).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Court court = (Court) o;
        return Double.compare(area, court.area) == 0 &&
                baseCost == court.baseCost &&
                courtNumber == court.courtNumber &&
                archive == court.archive &&
                rented == court.rented &&
                Objects.equals(courtId, court.courtId);
    }
}
