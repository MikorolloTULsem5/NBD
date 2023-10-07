package nbd.gV;

import nbd.gV.exceptions.MainException;

public class VolleyballCourt extends Court {
    public VolleyballCourt(double area, int baseCost, int courtNumber) throws MainException {
        super(area, baseCost, courtNumber);
    }

    @Override
    public double getActualReservationPrice() {
        return getBaseCost() * 1.2;
    }

    @Override
    public String getCourtInfo() {
        return super.getCourtInfo().replace("[-]", "siatkowki");
    }
}
