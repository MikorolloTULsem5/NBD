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
@CqlName(SchemaConst.RESERVATIONS_BY_CLIENT_TABLE)
@PropertyStrategy(mutable = false)
public class ReservationClientsDTO implements ReservationDTO {
    @PartitionKey
    private final UUID clientId;
    @ClusteringColumn
    private final Instant beginTime;
    @ClusteringColumn(1)
    private final UUID reservationId;

    private final Instant endTime;
    private final UUID courtId;
    private final double reservationCost;

    public ReservationClientsDTO(UUID clientId, Instant beginTime, UUID reservationId, Instant endTime,
                                 UUID courtId, double reservationCost) {
        this.clientId = clientId;
        this.beginTime = beginTime;
        this.reservationId = reservationId;
        this.endTime = endTime;
        this.courtId = courtId;
        this.reservationCost = reservationCost;
    }

    public static ReservationClientsDTO toDTO(Reservation reservation) {
        return new ReservationClientsDTO(
                reservation.getClient().getClientId(),
                reservation.getBeginTime().atZone(ZoneId.systemDefault()).toInstant(),
                reservation.getId(),
                reservation.getEndTime() != null ? reservation.getEndTime().atZone(ZoneId.systemDefault()).toInstant() : null,
                reservation.getCourt().getCourtId(),
                reservation.getReservationCost());
    }

    public static Reservation fromDTO(ReservationClientsDTO reservationDto, Client client, Court court) {
        Reservation reservation = new Reservation(reservationDto.getReservationId(), client, court, LocalDateTime.ofInstant(reservationDto.getBeginTime(), ZoneId.systemDefault()));

        if (reservationDto.getEndTime() != null) {
            reservation.endReservation(LocalDateTime.ofInstant(reservationDto.getEndTime(), ZoneId.systemDefault()));
        }
        return reservation;
    }
}
