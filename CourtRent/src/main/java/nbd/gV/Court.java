package nbd.gV;

import nbd.gV.exceptions.MainException;

public abstract class Court {
    private double area;
    private int baseCost;
    private final int courtNumber;
    private boolean archive = false;
    private boolean rented = false;

    public Court(double area, int baseCost, int courtNumber) {
        if (area <= 0.0 || baseCost < 0 || courtNumber < 1) {
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu boiska!");
        }
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
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

    public abstract double getActualReservationPrice();

    public String getCourtInfo() {
        return "Boisko nr %d przeznaczone do [-] o powierzchni %.2f i koszcie za rezerwację: %.2f PLN\n"
                .formatted(getCourtNumber(), getArea(), getActualReservationPrice());
    }

    public String getCourtTypeName() {
        return this.getClass().getSimpleName();
    }
}
