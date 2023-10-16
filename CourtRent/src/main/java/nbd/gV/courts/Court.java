package nbd.gV.courts;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import nbd.gV.exceptions.MainException;

import java.util.Formatter;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name="court")
@Access(value = AccessType.FIELD)
public class Court {
    @Id
    private UUID courtId;
    @NotEmpty
    private double area;
    @NotEmpty
    private int baseCost;
    @Column(nullable = false, unique = true)
    private int courtNumber;
    @NotEmpty
    private boolean archive = false;
    @NotEmpty
    private boolean rented = false;

    public Court(double area, int baseCost, int courtNumber) {
        if (area <= 0.0 || baseCost < 0 || courtNumber < 1) {
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu boiska!");
        }
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
        courtId = UUID.randomUUID();
    }

    public Court() {
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
}
