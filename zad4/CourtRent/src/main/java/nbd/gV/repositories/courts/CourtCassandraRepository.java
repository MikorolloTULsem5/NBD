package nbd.gV.repositories.courts;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import nbd.gV.courts.Court;
import nbd.gV.repositories.AbstractCassandraRepository;

import java.util.List;
import java.util.UUID;

import static nbd.gV.SchemaConst.ARCHIVE;
import static nbd.gV.SchemaConst.AREA;
import static nbd.gV.SchemaConst.BASE_COST;
import static nbd.gV.SchemaConst.COURTS_TABLE;
import static nbd.gV.SchemaConst.COURT_ID;
import static nbd.gV.SchemaConst.COURT_NUMBER;
import static nbd.gV.SchemaConst.RENTED;

public class CourtCassandraRepository extends AbstractCassandraRepository {
    public CourtCassandraRepository() {
        initSession();
        addKeyspace();

        SimpleStatement createCourts = SchemaBuilder.createTable(COURTS_TABLE)
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql(COURT_NUMBER), DataTypes.INT)
                .withClusteringColumn(CqlIdentifier.fromCql(RENTED), DataTypes.BOOLEAN)
                .withColumn(CqlIdentifier.fromCql(COURT_ID), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql(AREA), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql(BASE_COST), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql(ARCHIVE), DataTypes.BOOLEAN)
                .withClusteringOrder(CqlIdentifier.fromCql(RENTED), ClusteringOrder.ASC)
                .build();
        session.execute(createCourts);
    }

    protected CourtDao getDao() {
        CourtMapper courtMapper = new CourtMapperBuilder(session).build();
        return courtMapper.courtDao();
    }

    /*----------------------------------------------CRUD-------------------------------------------------------*/

    public void create(Court court) {
        getDao().create(court);
    }

    public Court read(int courtNumber) {
        return getDao().findCourt(courtNumber);
    }

    public Court readByUUID(UUID courtId) {
        return getDao().findCourtByUUID(courtId);
    }

    public Court readByUUID(String courtId) {
        return readByUUID(UUID.fromString(courtId));
    }

    public List<Court> readAll() {
        return getDao().findAllCourts().all();
    }

    public void update(Court court) {
        getDao().updateCourtRented(court);
    }

    public void delete(Court court) {
        getDao().deleteCourt(court);
    }
}
