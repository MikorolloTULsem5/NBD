package nbd.gV.clients;

import jakarta.persistence.Access;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.persistence.AccessType;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import nbd.gV.exceptions.MainException;

import java.util.UUID;

@Entity
@Table(name="client")
@Access(value = AccessType.FIELD)
public class Client {

    @Id
    private UUID clientID;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @Column(unique = true)
    @NotEmpty
    @Size(min = 11, max = 11)
    private String personalId;
    @Column(nullable = false)
    private boolean archive = false;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotNull
    private ClientType clientType;

    public Client(String firstName, String lastName, String personalId, ClientType clientType) {
        if (firstName.isEmpty() || lastName.isEmpty() || personalId.isEmpty() || clientType == null)
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");

        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.clientType = clientType;
        clientID = UUID.randomUUID();
    }

    public Client() {
    }

    public UUID getClientID() {
        return clientID;
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

    public String getPersonalId() {
        return personalId;
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
        return "Klient - %s %s o numerze PESEL %s\n".formatted(firstName, lastName, personalId);
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public int getClientMaxHours() {
        return clientType.getMaxHours();
    }
}
