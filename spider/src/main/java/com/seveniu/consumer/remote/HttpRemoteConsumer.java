package com.seveniu.consumer.remote;

import com.seveniu.consumer.ConsumerClient;
import com.seveniu.def.TaskStatus;
import com.seveniu.entity.data.Node;
import com.seveniu.entity.task.ConsumerConfig;
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

/**
 * Created by seveniu on 5/24/16.
 * RemoteConsumer
 */
public class HttpRemoteConsumer implements ConsumerClient {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ConsumerConfig remoteConsumerConfig;

    private RemoteRequest remoteRequest;

    public HttpRemoteConsumer(ConsumerConfig remoteConsumerConfig) {
        this.remoteConsumerConfig = remoteConsumerConfig;
        this.remoteRequest = new RemoteRequest();
    }

    @Override
    public boolean has(String url) {

        return Boolean.valueOf(remoteRequest.send(remoteConsumerConfig.getDuplicateUrl(), url));
    }

    @Override
    public void done(Node node) {
        remoteRequest.send(remoteConsumerConfig.getDoneUrl(), Json.toJson(node));
    }


    @Override
    public void statistic(TaskStatistic statistic) {
        remoteRequest.send(remoteConsumerConfig.getStatisticUrl(), Json.toJson(statistic));
    }

    @Override
    public void taskStatusChange(String taskId, TaskStatus taskStatus) {

    }

    @Override
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

        public RemoteRequest() {
            this.httpClient = HttpClients.createDefault();
        }

        private void close() throws IOException {
            httpClient.close();
        }

        public String send(String url, String body) {
            InputStream inputStream = null;
            CloseableHttpResponse response = null;
            try {
                HttpPost post = new HttpPost(url);
                if (body != null) {
                    post.setEntity(new StringEntity(body));
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
