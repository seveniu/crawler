package com.seveniu.service;

import com.seveniu.entity.task.ConsumerConfig;
import com.seveniu.entity.task.ResourceInfo;
import com.seveniu.entity.task.TaskInfo;

/**
 * Created by seveniu on 1/8/17.
 * *
 */
public interface CrawlerClient {
    void reg(String host, ConsumerConfig consumerConfig);

    String getRunningTasks();

    ResourceInfo getResourceInfo();

    void addTask(TaskInfo taskInfo);

    String getTaskSummary();
}
