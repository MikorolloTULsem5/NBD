package nbd.gV;

import nbd.gV.clients.Athlete;
import nbd.gV.clients.Client;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Coach;
import nbd.gV.clients.Normal;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;


public class ClientMapper {
    @BsonProperty("_id")
    private String clientID;
    @BsonProperty("firstname")
    private String firstName;
    @BsonProperty("lastname")
    private String lastName;
    @BsonProperty("personalid")
    private String personalId;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("clienttype")
    private String clientType;

    @BsonCreator
    public ClientMapper(@BsonProperty("_id") String clientID,
                        @BsonProperty("firstname") String firstName,
                        @BsonProperty("lastname") String lastName,
                        @BsonProperty("personalid") String personalId,
                        @BsonProperty("archive") boolean archive,
                        @BsonProperty("clienttype") String clientType) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.archive = archive;
        this.clientType = clientType;
    }

    public String getClientID() {
        return clientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPersonalId() {
        return personalId;
    }

    public boolean isArchive() {
        return archive;
    }

    public String getClientType() {
        return clientType;
    }

    public static ClientMapper toMongoClient(Client client) {
        return new ClientMapper(client.getClientID().toString(), client.getFirstName(),
                client.getLastName(), client.getPersonalId(), client.isArchive(),
                client.getClientType().getClientTypeName());
    }

    public static Client fromMongoClient(ClientMapper clientMapper) {
        ClientType type = switch (clientMapper.getClientType()) {
            case "Normal" -> new Normal();
            case "Athlete" -> new Athlete();
            case "Coach" -> new Coach();
            default -> null;
        };

        Client clientModel = new Client(UUID.fromString(clientMapper.getClientID()), clientMapper.getFirstName(),
                clientMapper.getLastName(), clientMapper.getPersonalId(), type);
        clientModel.setArchive(clientMapper.isArchive());
        return clientModel;
    }
}
