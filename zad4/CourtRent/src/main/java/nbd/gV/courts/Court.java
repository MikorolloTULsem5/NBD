package nbd.gV.courts;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import nbd.gV.SchemaConst;
import nbd.gV.exceptions.MainException;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(defaultKeyspace = SchemaConst.RESERVE_A_COURT_NAMESPACE)
@CqlName("courts")
@PropertyStrategy(mutable = false)
public class Court {
    @Setter(AccessLevel.NONE)
    @PartitionKey
    private int courtNumber;
    @ClusteringColumn
    private boolean rented = false;

    @Setter(AccessLevel.NONE)
    private UUID courtId;

    private double area;
    private int baseCost;

    private boolean archive = false;

    //Constructor for Cassandra
    public Court(int courtNumber, boolean rented, UUID courtId, double area, int baseCost, boolean archive) {
        this.courtNumber = courtNumber;
        this.rented = rented;
        this.courtId = courtId;
        this.area = area;
        this.baseCost = baseCost;
        this.archive = archive;
    }

    public Court(double area, int baseCost, int courtNumber) {
        if (area <= 0.0 || baseCost < 0 || courtNumber < 1) {
            throw new MainException("Niepoprawny parametr przy tworzeniu obiektu boiska!");
        }
        this.courtId = UUID.randomUUID();
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Court court = (Court) o;
        return Double.compare(area, court.area) == 0 &&
                baseCost == court.baseCost &&
                courtNumber == court.courtNumber &&
                archive == court.archive &&
                rented == court.rented &&
                Objects.equals(courtId, court.courtId);
    }
}
