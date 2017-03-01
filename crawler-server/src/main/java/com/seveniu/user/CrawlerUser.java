package com.seveniu.user;

import com.seveniu.entity.data.Node;
import com.seveniu.entity.task.CrawlerUserInfo;
import com.seveniu.entity.task.TaskInfo;
import com.seveniu.service.CrawlerClient;
import com.seveniu.service.CrawlerUserApi;
import com.seveniu.task.TaskStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by seveniu on 5/13/16.
 * Consumer
 */
public class CrawlerUser implements CrawlerUserApi {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String uuid; // 内部使用
    private String name; // 外部使用

    private UserTaskManager taskManager;
    private CrawlerClient client;

    private LinkedBlockingQueue<Runnable> dataQueue;
    private ExecutorService transferService;

    protected volatile STATUS status = STATUS.UN_START;

    public CrawlerUser(String name, CrawlerClient consumerClient) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
        this.uuid = name;
        this.client = consumerClient;
    }


    String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }


    public UserTaskManager getTaskManager() {
        return taskManager;
    }

    public void start() {
        dataQueue = new LinkedBlockingQueue<>();
        transferService = new ThreadPoolExecutor(6, 6,
                0L, TimeUnit.MILLISECONDS,
                dataQueue,
                r -> new Thread(r, getName() + "-transfer-thread"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        taskManager = new UserTaskManager(this);
        taskManager.start();
        status = STATUS.START;
        logger.info("consumer : {} start", name);
    }

    public void transfer(Node node) {

        transferService.execute(() -> {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            client.done(node);
        });
    }

    public CrawlerClient getClient() {
        return client;
    }

    void changeClient(CrawlerClient client) {
        this.uuid = UUID.randomUUID().toString();
        this.client.stop();
        this.client = client;
    }

    int waitSize() {
        return dataQueue.size();
    }

    public void stop() {
        logger.info("consumer : {} --- {}   stop ~~~~", name, uuid);
        status = STATUS.STOP;
        transferService.shutdownNow();
        this.dataQueue.clear();
        this.taskManager.stop();
        this.client.stop();
    }

    @Override
    public String toString() {
        return "Consumer{" +
                " name : " + getName() +
                "}";
    }

    @Override
    public List<TaskStatistic> getRunningTasks() {
        return this.taskManager.getRunningTaskInfo();
    }

    @Override
    public CrawlerUserInfo getResourceInfo() {
        return new CrawlerUserInfo(
                taskManager.getMaxRunning(),
                taskManager.getMaxWait(),
                taskManager.getCurRunningSize(),
                0
        );
    }

    @Override
    public void addTask(TaskInfo taskInfo) {
        taskManager.addTask(taskInfo);
    }

    public enum STATUS {
        STOP, START, UN_START
    }
}
