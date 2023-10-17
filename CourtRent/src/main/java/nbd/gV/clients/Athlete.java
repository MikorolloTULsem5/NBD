package nbd.gV.clients;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

@Entity
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
