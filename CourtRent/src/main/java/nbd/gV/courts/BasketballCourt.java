package nbd.gV.courts;

import nbd.gV.exceptions.MainException;

public class BasketballCourt extends Court {

    public BasketballCourt(double area, int baseCost, int courtNumber) throws MainException {
        super(area, baseCost, courtNumber);
    }

    @Override
    public double getActualReservationPrice() {
        return getBaseCost() * 1.3;
    }

    @Override
    public String getCourtInfo() {
        return super.getCourtInfo().replace("[-]", "koszykowki");
    }
}
