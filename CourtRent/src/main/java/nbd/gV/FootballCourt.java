package nbd.gV;

import nbd.gV.exceptions.MainException;

public class FootballCourt extends Court {
    public FootballCourt(double area, int baseCost, int courtNumber) throws MainException {
        super(area, baseCost, courtNumber);
    }

    @Override
    public double getActualReservationPrice() {
        return getBaseCost() * 1.5;
    }

    @Override
    public String getCourtInfo() {
        return super.getCourtInfo().replace("[-]", "pilki noznej");
    }
}
