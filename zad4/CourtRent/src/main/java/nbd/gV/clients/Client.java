package nbd.gV.clients;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import com.datastax.oss.driver.api.mapper.annotations.Transient;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import nbd.gV.SchemaConst;
import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.exceptions.MainException;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(defaultKeyspace = SchemaConst.RESERVE_A_COURT_NAMESPACE)
@CqlName("clients")
@PropertyStrategy(mutable = false)
public class Client {
    @PartitionKey
    @Setter(AccessLevel.NONE)
    private String personalId;
    @ClusteringColumn
    private String clientTypeName;

    @Setter(AccessLevel.NONE)
    private UUID clientId;
    private String firstName;
    private String lastName;
    private boolean archive = false;

    @Transient
    @Setter(AccessLevel.NONE)
    private ClientType clientType;

    //Constructor for Cassandra
    public Client(String personalId, String clientTypeName, UUID clientId, String firstName, String lastName, boolean archive) {
        this.personalId = personalId;
        this.clientTypeName = clientTypeName;
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.archive = archive;

        this.clientType = switch (clientTypeName.toLowerCase()) {
            case "normal" -> new Normal();
            case "athlete" -> new Athlete();
            case "coach" -> new Coach();
            default -> null;
        };
    }

    public Client(String firstName, String lastName, String personalId, String clientTypeName) {
        if (firstName.isEmpty() || lastName.isEmpty() || personalId.isEmpty() || clientTypeName.isEmpty()) {
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");
        }

        this.clientId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.clientTypeName = clientTypeName;

        this.clientType = switch (clientTypeName.toLowerCase()) {
            case "normal" -> new Normal();
            case "athlete" -> new Athlete();
            case "coach" -> new Coach();
            default -> null;
        };
    }

    public void setClientTypeName(String clientTypeName) {
        this.clientTypeName = clientTypeName;
        this.clientType = switch (clientTypeName.toLowerCase()) {
            case "normal" -> new Normal();
            case "athlete" -> new Athlete();
            case "coach" -> new Coach();
            default -> null;
        };
    }

    @Transient
    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    @Transient
    public int getClientMaxHours() {
        return clientType.getMaxHours();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return archive == client.archive &&
                Objects.equals(clientId, client.clientId) &&
                Objects.equals(firstName, client.firstName) &&
                Objects.equals(lastName, client.lastName) &&
                Objects.equals(personalId, client.personalId) &&
                Objects.equals(clientType.getClientTypeName(), client.clientType.getClientTypeName());
    }
}
