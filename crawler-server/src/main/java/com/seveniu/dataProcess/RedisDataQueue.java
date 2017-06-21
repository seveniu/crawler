package com.seveniu.dataProcess;

import com.seveniu.entity.data.Node;
import com.seveniu.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by seveniu on 10/11/16.
 * *
 */
@Service
public class RedisDataQueue implements DataQueue {

    private static final String PREFIX = "data-";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool jedisPool;

    @Autowired
    public RedisDataQueue(@Value("${crawler.redis.host}") String host,
                          @Value("${crawler.redis.port}") int port) {
        logger.info("data queue host : {} , port : {}", host, port);
        this.jedisPool = new JedisPool(host, port);
    }

    public void addData(String key, Node data) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.rpush(PREFIX + key, Json.toJson(data));
        }
    }
}
