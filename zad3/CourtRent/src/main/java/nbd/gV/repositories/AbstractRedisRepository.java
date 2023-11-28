package nbd.gV.repositories;

import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.exceptions.JedisException;

public abstract class AbstractRedisRepository<T>{

    private static JedisPooled pool;

    public AbstractRedisRepository() {
        connect();
    }

    public abstract boolean create(T mapper);
    public abstract T read(String id);

    public void close(){
        pool.close();
    }

    public void connect() {
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().build();
        pool = new JedisPooled(new HostAndPort("localhost", 6379), clientConfig);
    }

    protected boolean create(String id, String json){
        try {
            pool.set(id, json);
            pool.expire(id, 10);
        } catch (JedisException e){
            return false;
        }
        return true;
    }

    public boolean delete(String id){
        try {
            pool.del(id);
        } catch (JedisException e){
            return false;
        }
        return true;
    }

    protected String readById(String id){
        try {
            return pool.get(id);
        } catch (JedisException e){
            return null;
        }
    }

}