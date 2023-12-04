package nbd.gV.repositories;

import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.exceptions.JedisException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
        Properties prop = new Properties();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("src/main/resources/connection.properties");
            prop.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().build();
        String name = prop.getProperty("redisHost");
        int port = Integer.parseInt(prop.getProperty("redisPort"));
        pool = new JedisPooled(new HostAndPort(name, port), clientConfig);
    }

    protected boolean create(String id, String json){
        try {
            pool.set(id, json);
            pool.expire(id, 600);
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