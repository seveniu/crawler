package com.seveniu.service.task;

import com.seveniu.entity.task.TaskInfo;
import com.seveniu.util.Json;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by seveniu on 10/11/16.
 * *
 */
@Service
public class TaskQueue {

    private static final String PREFIX = "task-";

    private JedisPool pool;

    public TaskQueue(@Value("${crawler.redis.host}") String host, @Value("${crawler.redis.port}") int port) {
        this.pool = new JedisPool(host, port);
    }

    public String pop(String name) {
        try (Jedis jedis = this.pool.getResource()) {
            return jedis.lpop(PREFIX + name);
        }
    }

    public void addTask(String key, TaskInfo taskInfo) {
        try (Jedis jedis = this.pool.getResource()) {
            if (taskInfo.getPriority() > 0) {
                jedis.lpush(key, Json.toJson(taskInfo));
            } else {
                jedis.rpush(key, Json.toJson(taskInfo));
            }
        }
    }

    public int getWaitSize(String name) {
        try (Jedis jedis = this.pool.getResource()) {
            return jedis.llen(PREFIX + name).intValue();
        }
    }

}
