package nbd.gV.repositories.courts;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.mapper.annotations.Update;

import nbd.gV.courts.Court;

import java.util.UUID;

@Dao
public interface CourtDao {
    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void create(Court court);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    Court findCourt(int courtNumber);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = CourtProvider.class, entityHelpers = {Court.class},
            providerMethod = "findCourtByUUID")
    Court findCourtByUUID(UUID courtId);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    PagingIterable<Court> findAllCourts();

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Update
    void updateCourt(Court court);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = CourtProvider.class, entityHelpers = {Court.class},
            providerMethod = "updateCourtRented")
    void updateCourtRented(Court court);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Delete
    void deleteCourt(Court court);
}
