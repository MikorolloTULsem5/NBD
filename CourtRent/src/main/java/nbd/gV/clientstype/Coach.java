package nbd.gV.clientstype;

public class Coach extends ClientType {
    @Override
    public double applyDiscount(double price) {
        return 10 + 0.05 * price;
    }

    @Override
    public int getMaxHours() {
        return 12;
    }
}
