package nbd.gV.repositories.courts;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface CourtMapper {
    @DaoFactory
    CourtDao courtDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    CourtDao courtDao();
}
