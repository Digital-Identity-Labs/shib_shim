package com.digitalidentitylabs.shabti.shim;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DemandStorage {

    // Redis Pool that will, I think, be threadsafe...
    private static JedisPool redisPool = null;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public DemandStorage() {

        if (redisPool == null) {
            redisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
        }

    }

    public DemandStorage(String host) {

        this();

        redisPool = new JedisPool(new JedisPoolConfig(), host);

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

            redis.close();

        }


    }

    public void write(Demand demand) {

        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            String jsonText = demand.toJSON();

            redis.setex(demand.id, 60, jsonText);

            log.info("Writing JSON to storage: {}", jsonText); // TODO: Change to debug

        } finally {

            redis.close();

        }

    }

    public Demand read(String token) throws IOException {

        // Scoping on try?
        Jedis redis = null;

        try {

            redis = redisPool.getResource();

            String jsonText = redis.get(token);

            log.info("Read JSON from storage: {}", jsonText); // TODO: Change to debug


            if (jsonText == null) {
                log.warn("Demand not found, returning MissingDemand for token {}", token);
                return new MissingDemand();
            }

            return new IncomingDemand(jsonText);

        } finally {

            redis.close();

        }

    }
}