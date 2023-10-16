package nbd.gV.clients;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import nbd.gV.exceptions.MainException;

import java.util.UUID;

@Entity
@Table(name="client")
@Access(value = AccessType.FIELD)
public class Client {

    @Id
    private UUID clientID;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String personalID;
    @Column(nullable = false)
    private boolean archive = false;
    @Embedded
    @AttributeOverride(name = "clientType", column = @Column(name="client_type"))
    @Column(nullable = false)
    private ClientType clientType;

    public UUID getClientID() {
        return clientID;
    }

    public Client(String firstName, String lastName, String personalID, ClientType clientType) {
        if (firstName.isEmpty() || lastName.isEmpty() || personalID.isEmpty() || clientType == null)
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");

        this.firstName = firstName;
        this.lastName = lastName;
        this.personalID = personalID;
        this.clientType = clientType;
        clientID = UUID.randomUUID();
    }

    public Client() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!firstName.isEmpty()) {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!lastName.isEmpty()) {
            this.lastName = lastName;
        }
    }

    public String getPersonalID() {
        return personalID;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        if (clientType != null) {
            this.clientType = clientType;
        }
    }

    public String getClientInfo() {
        return "Klient - %s %s o numerze PESEL %s\n".formatted(firstName, lastName, personalID);
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public int getClientMaxHours() {
        return clientType.getMaxHours();
    }
}
