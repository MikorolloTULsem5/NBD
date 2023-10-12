package nbd.gV.courts;

import nbd.gV.exceptions.MainException;

public class TennisCourt extends Court {
    public TennisCourt(double area, int baseCost, int courtNumber) throws MainException {
        super(area, baseCost, courtNumber);
    }

    @Override
    public double getActualReservationPrice() {
        return getBaseCost() * 1.1;
    }

    @Override
    public String getCourtInfo() {
        return super.getCourtInfo().replace("[-]", "tenisa");
    }
}
