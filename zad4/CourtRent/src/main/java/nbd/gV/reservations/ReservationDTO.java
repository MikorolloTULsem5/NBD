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

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity(defaultKeyspace = SchemaConst.RESERVE_A_COURT_NAMESPACE)
@CqlName("reservations_by_client")
@PropertyStrategy(mutable = false)
public class ReservationDTO {
    @PartitionKey
    private final UUID clientId;
    @ClusteringColumn
    private final LocalDateTime endTime;
    @ClusteringColumn
    private final UUID reservationId;

    private final LocalDateTime beginTime;
    private final UUID courtId;
    private final double reservationCost;

    public ReservationDTO(UUID clientId, LocalDateTime endTime, UUID reservationId, LocalDateTime beginTime,
                          UUID courtId, double reservationCost) {
        this.clientId = clientId;
        this.endTime = endTime;
        this.reservationId = reservationId;
        this.beginTime = beginTime;
        this.courtId = courtId;
        this.reservationCost = reservationCost;
    }

    public static ReservationDTO toDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getClient().getClientId(),
                reservation.getEndTime(),
                reservation.getId(),
                reservation.getBeginTime(),
                reservation.getCourt().getCourtId(),
                reservation.getReservationCost());
    }

    public static Reservation fromDTO(ReservationDTO reservationDto, Client client, Court court) {
        Reservation reservation = new Reservation(client, court, reservationDto.getBeginTime());

        if (reservationDto.getEndTime() != null) {
            reservation.endReservation(reservationDto.getEndTime());
        }
        return reservation;
    }
}
