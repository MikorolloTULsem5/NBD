package nbd.gV.repositories.reservations;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;

import com.datastax.oss.driver.api.mapper.annotations.Update;
import nbd.gV.courts.Court;
import nbd.gV.reservations.Reservation;
import nbd.gV.reservations.ReservationClientsDTO;
import nbd.gV.reservations.ReservationCourtsDTO;
import nbd.gV.reservations.ReservationDTO;

import java.util.UUID;

@Dao
public interface ReservationDao {
    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void createClientReservation(ReservationClientsDTO reservationClientsDTO);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void createCourtReservation(ReservationCourtsDTO reservationCourtsDTO);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ReservationProvider.class,
            entityHelpers = {ReservationClientsDTO.class, ReservationCourtsDTO.class},
            providerMethod = "findReservationByUUID")
    ReservationDTO findReservationByUUID(UUID reservationId);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    PagingIterable<ReservationClientsDTO> findAllReservationsByClients();

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ReservationProvider.class,
            entityHelpers = {ReservationClientsDTO.class, ReservationCourtsDTO.class},
            providerMethod = "findAllReservationsByClientsFilter")
    PagingIterable<ReservationClientsDTO> findAllReservationsByClientsFilter(SimpleStatement statement);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    PagingIterable<ReservationCourtsDTO> findAllReservationsByCourts();

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ReservationProvider.class,
            entityHelpers = {ReservationClientsDTO.class, ReservationCourtsDTO.class},
            providerMethod = "findAllReservationsByCourtsFilter")
    PagingIterable<ReservationCourtsDTO> findAllReservationsByCourtsFilter(SimpleStatement statement);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ReservationProvider.class,
            entityHelpers = {ReservationClientsDTO.class, ReservationCourtsDTO.class},
            providerMethod = "updateReservation")
    void updateReservation(Reservation reservation);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void deleteReservation(ReservationClientsDTO reservationClientsDTO);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void deleteReservation(ReservationCourtsDTO reservationCourtsDTO);
}
