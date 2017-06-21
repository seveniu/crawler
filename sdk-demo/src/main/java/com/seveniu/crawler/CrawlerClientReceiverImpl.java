package com.seveniu.crawler;

import com.seveniu.service.CrawlerClientReceiver;
import com.seveniu.def.TaskStatus;
import com.seveniu.entity.data.Node;
import com.seveniu.task.TaskStatistic;
import org.springframework.stereotype.Service;

/**
 * Created by seveniu on 1/15/17.
 * *
 */
@Service
public class CrawlerClientReceiverImpl implements CrawlerClientReceiver {
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
