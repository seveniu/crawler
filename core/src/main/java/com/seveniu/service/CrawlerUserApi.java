package com.seveniu.service;

import com.seveniu.entity.task.CrawlerUserInfo;
import com.seveniu.entity.task.TaskInfo;
import com.seveniu.task.TaskStatistic;

import java.util.List;

/**
 * Created by seveniu on 1/8/17.
 * *
 */
public interface CrawlerUserApi {

    List<TaskStatistic> getRunningTasks();

    CrawlerUserInfo getResourceInfo();

    void addTask(TaskInfo taskInfo);

}
