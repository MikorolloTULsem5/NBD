package nbd.gV.repositories;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
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
            return jsonb.fromJson(result,CourtMapper.class);
        }
    }

    @Override
    public boolean update(CourtMapper mapper) {
        return super.update(prefix + mapper.getCourtId(), jsonb.toJson(mapper));
    }

    @Override
    public String getPrefix(){
        return prefix;
    }
}
