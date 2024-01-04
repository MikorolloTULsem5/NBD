package nbd.gV.repositories.reservations;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface ReservationMapper {
    @DaoFactory
    ReservationDao reservationDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    ReservationDao reservationDao();
}
