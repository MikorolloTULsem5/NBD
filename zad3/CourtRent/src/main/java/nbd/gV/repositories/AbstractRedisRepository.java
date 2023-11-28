package nbd.gV.repositories;

import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;

public abstract class AbstractRedisRepository<T>{

    private static JedisPooled pool;

    public AbstractRedisRepository() {
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().build();
        pool = new JedisPooled(new HostAndPort("localhost", 6379), clientConfig);
    }

    public abstract boolean create(T mapper);
    public abstract T read(String id);
     public abstract boolean update(T mapper);

    protected boolean create(String id, String json){
        try {
            pool.set(id, json);
            pool.expire(id, 10);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean delete(String id){
        try {
            pool.jsonDel(id);
        } catch (Exception e){
            return false;
        }
        return true;
    }
    protected boolean update(String id, String json){
        return create(id, json);
    }

    protected String readById(String id){
        return pool.get(id);
    }

    public abstract String getPrefix();
}