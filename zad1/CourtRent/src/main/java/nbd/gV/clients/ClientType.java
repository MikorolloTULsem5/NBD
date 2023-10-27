package nbd.gV.clients;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ClientType {

//    @Id
//    private UUID clientTypeUUID;
    @Id
    private String name;
    public ClientType(){
//        clientTypeUUID = UUID.randomUUID();
        name = this.getClass().getSimpleName();
    }
    public abstract double applyDiscount(double price);

    public abstract int getMaxHours();

    public String getClientTypeName() {
//        return this.getClass().getSimpleName();
    return name;
    }

    public String getTypeInfo() {
        return "%s %s wynosi %d%n".formatted("Maksymalna liczba godzin rezerwacji dla typu",
                name, getMaxHours());
    }
}
