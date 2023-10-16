package nbd.gV.clients;

import jakarta.persistence.Embeddable;

@Embeddable
public class Athlete extends ClientType {
    @Override
    public double applyDiscount(double price) {
        return 10;
    }

    @Override
    public int getMaxHours() {
        return 6;
    }
}
