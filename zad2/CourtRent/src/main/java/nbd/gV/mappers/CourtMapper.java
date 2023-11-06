package nbd.gV.mappers;

import nbd.gV.courts.Court;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.UUID;


public class CourtMapper {
    @BsonProperty("_id")
    private String courtId;
    @BsonProperty("area")
    private double area;
    @BsonProperty("basecost")
    private int baseCost;
    @BsonProperty("courtnumber")
    private int courtNumber;
    @BsonProperty("archive")
    private boolean archive;
    @BsonProperty("rented")
    private boolean rented;

    @BsonCreator
    public CourtMapper(@BsonProperty String courtId,
                       @BsonProperty double area,
                       @BsonProperty int baseCost,
                       @BsonProperty int courtNumber,
                       @BsonProperty boolean archive,
                       @BsonProperty boolean rented) {
        this.courtId = courtId;
        this.area = area;
        this.baseCost = baseCost;
        this.courtNumber = courtNumber;
        this.archive = archive;
        this.rented = rented;
    }

    public String getCourtId() {
        return courtId;
    }

    public double getArea() {
        return area;
    }

    public int getBaseCost() {
        return baseCost;
    }

    public int getCourtNumber() {
        return courtNumber;
    }

    public boolean isArchive() {
        return archive;
    }

    public boolean isRented() {
        return rented;
    }

    public static CourtMapper toMongoCourt(Court court) {
        return new CourtMapper(court.getCourtId().toString(), court.getArea(), court.getBaseCost(),
                court.getCourtNumber(), court.isArchive(), court.isRented());
    }

    public static Court fromMongoCourt(CourtMapper courtMapper) {
        Court courtModel = new Court(UUID.fromString(courtMapper.getCourtId()), courtMapper.getArea(),
                courtMapper.getBaseCost(), courtMapper.getCourtNumber());
        courtModel.setArchive(courtModel.isArchive());
        courtModel.setRented(courtModel.isRented());
        return courtModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourtMapper that = (CourtMapper) o;
        return Double.compare(area, that.area) == 0 &&
                baseCost == that.baseCost &&
                courtNumber == that.courtNumber &&
                archive == that.archive &&
                rented == that.rented &&
                Objects.equals(courtId, that.courtId);
    }
}
