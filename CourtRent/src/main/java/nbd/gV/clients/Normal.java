package nbd.gV.clients;

import jakarta.persistence.Embeddable;

@Embeddable
public class Normal extends ClientType {
    @Override
    public double applyDiscount(double price) {
        return 0;
    }

    @Override
    public int getMaxHours() {
        return 3;
    }
}
