package com.digitalidentitylabs.shabti.shim;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;


class ShabtiShimStorage {


    // Redis Pool that will, I think, be threadsafe...
    private static JedisPool redisPool = null;

    public ShabtiShimStorage() {

        redisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);

    }

    public ShabtiShimStorage(String host, int port) {

        redisPool = new JedisPool(new JedisPoolConfig(), host, port);

    }

    public ShabtiShimStorage(String host, int port, String password) {

        redisPool = new JedisPool(new JedisPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, password);

    }

    public void destroy() {

        redisPool.destroy();

    }

    public void delete(String token) {

        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            redis.del(token);

        } finally {

            redisPool.returnResource(redis);

        }


    }

    public void write(String token, String record) {

        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            redis.setex(token, 60, record);

        } finally {

            redisPool.returnResource(redis);

        }

    }

    public String read(String token) {

        // Scoping on try?
        Jedis redis = null;

        try {

            redis = redisPool.getResource();
            String text = redis.get(token);

            return text;

        } finally {

            redisPool.returnResource(redis);

        }

    }
}
