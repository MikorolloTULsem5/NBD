package nbd.gV.courts;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.AccessType;

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
    @Column(nullable = false)
    private double area;
    @Column(nullable = false)
    private int baseCost;
    @Column(nullable = false, unique = true)
    private int courtNumber;
    @Column(nullable = false)
    private boolean archive = false;
    @Column(nullable = false)
    private boolean rented = false;
    public UUID getCourtId() {
        return courtId;
    }

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
