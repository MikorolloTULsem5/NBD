package nbd.gV.clients;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ClientType {
    @Id
    private UUID clientTypeUUID;
    public ClientType(){
        clientTypeUUID = UUID.randomUUID();
    }
    public abstract double applyDiscount(double price);

    public abstract int getMaxHours();

    public String getClientTypeName() {
        return this.getClass().getSimpleName();
    }

    public String getTypeInfo() {
        return "%s %s wynosi %d%n".formatted("Maksymalna liczba godzin rezerwacji dla typu",
                getClientTypeName(), getMaxHours());
    }
}
