package com.seveniu.crawlClient;

import com.seveniu.entity.task.TaskInfo;
import com.seveniu.util.Json;
import redis.clients.jedis.Jedis;

/**
 * Created by seveniu on 10/21/16.
 * *
 */
public class TaskQueue {
    private final static String PREFIX = "task-";
    private Jedis jedis;
    private String host;
    private int port;
    private String key;

    public TaskQueue(String host, int port, String key) {
        this.host = host;
        this.port = port;
        this.key = PREFIX + key;
        this.jedis = new Jedis(host,port);
    }

    public void addTask(TaskInfo taskInfo) {
        if(taskInfo.getPriority() > 0) {
            jedis.lpush(key, Json.toJson(taskInfo));
        } else {
            jedis.rpush(key, Json.toJson(taskInfo));
        }
    }
}
