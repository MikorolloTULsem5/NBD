package nbd.gV;

import nbd.gV.exceptions.MainException;
import nbd.gV.clientstype.ClientType;

public class Client {
    private String firstName;
    private String lastName;
    private String personalID;
    private boolean archive = false;

    private ClientType clientType;

    public Client(String firstName, String lastName, String personalID, ClientType clientType) throws MainException {
        if (!firstName.isEmpty() || !lastName.isEmpty() || !personalID.isEmpty() || clientType == null)
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");

        this.firstName = firstName;
        this.lastName = lastName;
        this.personalID = personalID;
        this.clientType = clientType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPersonalID() {
        return personalID;
    }

    public boolean isArchive() {
        return archive;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setFirstName(String firstName) {
        if (!firstName.isEmpty()) {
            this.firstName = firstName;
        }
    }

    public void setLastName(String lastName) {
        if (!lastName.isEmpty()) {
            this.lastName = lastName;
        }
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public void setClientType(ClientType clientType) {
        if (clientType != null) {
            this.clientType = clientType;
        }
    }

    public String getClientInfo() {
        return "Klient - %s %s o numerze PESEL %s%n".formatted(firstName, lastName, personalID);
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public int getMaxClientHours() {
        return clientType.getMaxHours();
    }
}
