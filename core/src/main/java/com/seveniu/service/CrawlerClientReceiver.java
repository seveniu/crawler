package com.seveniu.service;

import com.seveniu.def.TaskStatus;
import com.seveniu.entity.data.Node;
import com.seveniu.task.TaskStatistic;

/**
 * Created by seveniu on 5/26/16.
 * Consumer
 */
public interface CrawlerClientReceiver {

    boolean has(String url);

    void done(Node node);

    void statistic(TaskStatistic statistic);

    void taskStatusChange(String taskId, TaskStatus taskStatus);

    void stop();

}
