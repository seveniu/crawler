package com.seveniu.user.remote;

import com.seveniu.def.TaskStatus;
import com.seveniu.entity.data.Node;
import com.seveniu.service.CrawlerClientReceiver;
import com.seveniu.service.RequestBody;
import com.seveniu.task.TaskStatistic;
import com.seveniu.util.Json;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seveniu on 5/24/16.
 * RemoteConsumer
 */
public class HttpRemoteConsumer implements CrawlerClientReceiver {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private RemoteRequest remoteRequest;
    private String requestUrl;

    public HttpRemoteConsumer(String host) {
        this.requestUrl = host + "/crawler/consumer";
        this.remoteRequest = new RemoteRequest();
    }


    @Override
    public boolean has(String url) {
        RequestBody requestBody = new RequestBody("has", url);
        return Boolean.valueOf(remoteRequest.send(requestUrl, requestBody));
    }

    @Override
    public void done(Node node) {
        RequestBody requestBody = new RequestBody("done", Json.toJson(node));
        remoteRequest.send(requestUrl, requestBody);
    }


    @Override
    public void statistic(TaskStatistic statistic) {
        RequestBody requestBody = new RequestBody("statistic", Json.toJson(statistic));
        remoteRequest.send(requestUrl, requestBody);
    }

    @Override
    public void taskStatusChange(String taskId, TaskStatus taskStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskId", taskId);
        map.put("status", taskStatus);
        RequestBody requestBody = new RequestBody("taskStatusChange", Json.toJson(map));
        remoteRequest.send(requestUrl, requestBody);
    }

    public void stop() {
        try {
            remoteRequest.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("close http request error : {}", e.getMessage());
        }
    }

    private class RemoteRequest {
        CloseableHttpClient httpClient;

        RemoteRequest() {
            this.httpClient = HttpClients.createDefault();
        }

        private void close() throws IOException {
            httpClient.close();
        }

        String send(String url, RequestBody body) {
            InputStream inputStream = null;
            CloseableHttpResponse response = null;
            try {
                HttpPost post = new HttpPost(url);
                if (body != null) {
                    post.setEntity(new StringEntity(Json.toJson(body)));
                }
                response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                return IOUtils.toString(inputStream, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (response != null) {
                        response.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
