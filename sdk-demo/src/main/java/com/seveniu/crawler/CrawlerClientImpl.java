package com.seveniu.crawler;

import com.seveniu.service.CrawlerClient;
import com.seveniu.def.TaskStatus;
import com.seveniu.entity.data.Node;
import com.seveniu.task.TaskStatistic;
import org.springframework.stereotype.Service;

/**
 * Created by seveniu on 1/15/17.
 * *
 */
@Service
public class CrawlerClientImpl implements CrawlerClient {
    @Override
    public boolean has(String url) {
        return false;
    }

    @Override
    public void done(Node node) {

    }

    @Override
    public void statistic(TaskStatistic statistic) {

    }

    @Override
    public void taskStatusChange(String taskId, TaskStatus taskStatus) {

    }

    @Override
    public void stop() {

    }
}
