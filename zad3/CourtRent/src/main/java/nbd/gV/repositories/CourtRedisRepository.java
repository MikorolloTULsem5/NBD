package nbd.gV.repositories;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import nbd.gV.courts.Court;
import nbd.gV.mappers.CourtMapper;

public class CourtRedisRepository extends AbstractRedisRepository<CourtMapper> {

    private final Jsonb jsonb = JsonbBuilder.create();
    private static final String prefix = "court:";
    public CourtRedisRepository(){
        super();
    }

    @Override
    public boolean create(CourtMapper mapper) {
        return super.create(prefix+mapper.getCourtId(), jsonb.toJson(mapper));
    }

    @Override
    public CourtMapper read(String id) {
        String result = super.readById(prefix+id);
        if(result == null) return null;
        else {
            CourtMapper temp = jsonb.fromJson(result,CourtMapper.class);
            return temp;
        }
    }

    @Override
    public boolean update(CourtMapper mapper) {
        return super.update(prefix + mapper.getCourtId().toString(), jsonb.toJson(mapper));
    }

    @Override
    public String getPrefix(){
        return prefix;
    }
}
