package com.seveniu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.seveniu.entity.task.CrawlerUserInfo;
import com.seveniu.entity.task.TaskInfo;
import com.seveniu.service.CrawlerServer;
import com.seveniu.service.CrawlerClient;
import com.seveniu.service.task.TaskQueue;
import com.seveniu.task.TaskStatistic;
import com.seveniu.util.Json;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by seveniu on 7/3/16.
 * CrawlClient
 */
@Component
public class CrawlHttpServerImpl implements CrawlerServer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HttpClient httpClient;
    private String host = "http://127.0.0.1:20001";

    private DataQueue dataQueue;
    private String uuid;
    private String name;
    private final TaskQueue taskQueue;

    @Autowired
    public CrawlHttpServerImpl(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }


    @Override
    public String reg(String name, String host) {
        this.host = host;
        this.name = name;
        this.uuid = post(host + "/api/consumer/reg", name);
        return uuid;
    }

    @Override
    public List<TaskStatistic> getRunningTasks(String uuid) {
        return Json.toObject(requestGet(host + "/api/consumer/running-task?uuid=" + uuid), new TypeReference<List<TaskStatistic>>() {
        });
    }

    @Override
    public CrawlerUserInfo getResourceInfo(String uuid) {
        return Json.toObject(requestGet(host + "/api/consumer/resource-info?uuid=" + uuid), CrawlerUserInfo.class);
    }

    public void addTask(String uuid, TaskInfo taskInfo) {
        taskQueue.addTask(name, taskInfo);
    }

    public String getTaskSummary() {
        return requestGet(host + "/api/consumer/task-summary?uuid=" + uuid);
    }

    private String requestGet(String url) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(get);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException("request error : " + e.getMessage(), e);
        }
    }

    private String post(String url, String body) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        try {
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException("request error : " + e.getMessage(), e);
        }
    }

    public void setDateQueueThread(int num) {
        this.dataQueue.setThreadNum(num);
    }

    public void start(String name, CrawlerClient crawlerClient) {
        this.name = name;
        this.dataQueue = new DataQueue(name, crawlerClient);
        this.dataQueue.start();
        initHttp();
    }

    private void initHttp() {

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setDefaultMaxPerRoute(20);
        httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }


}
