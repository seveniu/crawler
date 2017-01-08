package com.seveniu;

import com.seveniu.consumer.Consumer;
import com.seveniu.crawlClient.TaskQueue;
import com.seveniu.entity.task.ConsumerConfig;
import com.seveniu.entity.task.ResourceInfo;
import com.seveniu.entity.task.TaskInfo;
import com.seveniu.service.CrawlerClient;
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

import java.io.IOException;

/**
 * Created by seveniu on 7/3/16.
 * CrawlClient
 */
public class CrawlClientImpl implements CrawlerClient {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HttpClient httpClient;
    private String host = "http://127.0.0.1:20001";

    private TaskQueue taskQueue;
    private DataQueue dataQueue;
    private String uuid;


    public void reg(String host, ConsumerConfig consumerConfig) {
        this.host = host;
        this.uuid = post(host + "/api/consumer/reg", Json.toJson(consumerConfig));
    }

    public String getRunningTasks() {
        return requestGet(host + "/api/consumer/running-task?uuid=" + uuid);
    }

    public ResourceInfo getResourceInfo() {
        return Json.toObject(requestGet(host + "/api/consumer/resource-info?uuid=" + uuid), ResourceInfo.class);
    }

    public void addTask(TaskInfo taskInfo) {
        taskQueue.addTask(taskInfo);
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

    public void start(String name, String queueHost, int queuePort, Consumer consumer) {
        this.taskQueue = new TaskQueue(queueHost, queuePort, name);
        this.dataQueue = new DataQueue(name, consumer);
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
