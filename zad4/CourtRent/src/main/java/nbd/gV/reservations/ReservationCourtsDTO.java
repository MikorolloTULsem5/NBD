package nbd.gV.reservations;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import lombok.Getter;
import lombok.Setter;
import nbd.gV.SchemaConst;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
@Setter
@Entity(defaultKeyspace = SchemaConst.RESERVE_A_COURT_NAMESPACE)
@CqlName(SchemaConst.RESERVATIONS_BY_COURT_TABLE)
@PropertyStrategy(mutable = false)
public class ReservationCourtsDTO {
    @PartitionKey
    private final UUID courtId;
    @ClusteringColumn
    private final Instant beginTime;
    @ClusteringColumn(1)
    private final UUID reservationId;

    private final Instant endTime;
    private final UUID clientId;
    private final double reservationCost;

    public ReservationCourtsDTO(UUID courtId, Instant beginTime, UUID reservationId, Instant endTime,
                                 UUID clientId, double reservationCost) {
        this.courtId = courtId;
        this.beginTime = beginTime;
        this.reservationId = reservationId;
        this.endTime = endTime;
        this.clientId = clientId;
        this.reservationCost = reservationCost;
    }

    public static ReservationCourtsDTO toDTO(Reservation reservation) {
        return new ReservationCourtsDTO(
                reservation.getCourt().getCourtId(),
                reservation.getBeginTime().atZone(ZoneId.systemDefault()).toInstant(),
                reservation.getId(),
                reservation.getEndTime() != null ? reservation.getEndTime().atZone(ZoneId.systemDefault()).toInstant() : null,
                reservation.getClient().getClientId(),
                reservation.getReservationCost());
    }

    public static Reservation fromDTO(ReservationClientsDTO reservationDto, Client client, Court court) {
        Reservation reservation = new Reservation(client, court, LocalDateTime.ofInstant(reservationDto.getBeginTime(), ZoneId.systemDefault()));

        if (reservationDto.getEndTime() != null) {
            reservation.endReservation(LocalDateTime.ofInstant(reservationDto.getEndTime(), ZoneId.systemDefault()));
        }
        return reservation;
    }
}
