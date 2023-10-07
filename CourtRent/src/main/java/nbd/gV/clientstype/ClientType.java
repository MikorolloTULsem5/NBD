package nbd.gV.clientstype;

public abstract class ClientType {
    public abstract double applyDiscount(double price);

    public abstract int getMaxHours();

    public String getTypeInfo() {
        return "%s %s wynosi %d%n".formatted("Maksymalna liczba godzin rezerwacji dla typu", getClientTypeName(), getMaxHours());
    }

    public String getClientTypeName() {
        return this.getClass().getSimpleName();
    }

}
