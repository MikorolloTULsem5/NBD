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
        return super.create(prefix, jsonb.toJson(mapper));
    }

    @Override
    public CourtMapper read(String id) {
        return  jsonb.fromJson(super.readById(prefix+id), CourtMapper.class) ;
    }

    @Override
    public String getPrefix(){
        return prefix;
    }
}
