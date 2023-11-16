package nbd.gV.clients;

import nbd.gV.exceptions.MainException;

import java.util.Objects;
import java.util.UUID;

public class Client {

    private UUID clientID;

    private String firstName;

    private String lastName;

    private String personalId;

    private boolean archive = false;

    private ClientType clientType;

    public Client(String firstName, String lastName, String personalId, ClientType clientType) {
        if (firstName.isEmpty() || lastName.isEmpty() || personalId.isEmpty() || clientType == null)
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");

        this.clientID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.clientType = clientType;
    }

    public Client(UUID uuid, String firstName, String lastName, String personalId, ClientType clientType) {
        this(firstName, lastName, personalId, clientType);
        this.clientID = uuid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return archive == client.archive &&
                Objects.equals(clientID, client.clientID) &&
                Objects.equals(firstName, client.firstName) &&
                Objects.equals(lastName, client.lastName) &&
                Objects.equals(personalId, client.personalId) &&
                Objects.equals(clientType.getClientTypeName(), client.clientType.getClientTypeName());

    }
}
