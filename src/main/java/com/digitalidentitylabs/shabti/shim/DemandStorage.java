package com.digitalidentitylabs.shabti.shim;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.IOException;

public class DemandStorage {

    // Redis Pool that will, I think, be threadsafe...
    private static JedisPool redisPool = null;



    public DemandStorage() {

        if (redisPool == null) {
            redisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
        }

    }

    public DemandStorage(String host, int port) {

        this();

        redisPool = new JedisPool(new JedisPoolConfig(), host, port);

    }

    public DemandStorage(String host, int port, String password) {

        this();

        redisPool = new JedisPool(new JedisPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, password);

    }

    public void destroy() {

        redisPool.destroy();

    }

    public void delete(Demand demand) {

        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            redis.del(demand.id);

        } finally {

            redisPool.returnResource(redis);

        }


    }

    public void write(Demand demand) {

        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            redis.setex(demand.id, 60, demand.toJSON());

        } finally {

            redisPool.returnResource(redis);

        }

    }

    public Demand read(String token) throws IOException {

        // Scoping on try?
        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            String jsonText = redis.get(token);

            return new Demand(jsonText);

        } finally {

            redisPool.returnResource(redis);

        }

    }
}