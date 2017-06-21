package com.seveniu.user;

import com.seveniu.SpiderRegulate;
import com.seveniu.entity.task.CrawlerUserInfo;
import com.seveniu.entity.task.TaskInfo;
import com.seveniu.service.CrawlerClientReceiver;
import com.seveniu.service.CrawlerServer;
import com.seveniu.service.task.TaskQueue;
import com.seveniu.task.TaskStatistic;
import com.seveniu.user.remote.HttpRemoteConsumer;
import com.seveniu.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by seveniu on 5/13/16.
 * ConsumerManager
 */
@Component
public class UserManager implements DisposableBean, CrawlerServer {

    private Logger logger = LoggerFactory.getLogger(UserManager.class);
    private ConcurrentHashMap<String, CrawlerUser> consumerMap = new ConcurrentHashMap<>();

    @Value("${dataQueue:false}")
    boolean useDataQueue;
    private final TaskQueue taskQueue;

    @Autowired
    public UserManager(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    private final Object regLock = new Object();


    private void removeConsumer(String uuid) {
        CrawlerUser consumer = this.consumerMap.remove(uuid);
        consumer.stop();
    }

    private CrawlerUser getConsumerByUUID(String uuid) {
        return consumerMap.get(uuid);
    }

    private CrawlerUser getConsumerByName(String name) {
        for (CrawlerUser consumer1 : consumerMap.values()) {
            if (consumer1.getName().equals(name)) {
                return consumer1;
            }
        }
        return null;
    }

    public Collection<CrawlerUser> getAllConsumer() {
        return consumerMap.values();
    }


    @Override
    public void destroy() throws Exception {
        consumerMap.values().forEach(CrawlerUser::stop);
    }

    @Override
    public String reg(String name, String host) {
        synchronized (regLock) {
            CrawlerUser consumer = getConsumerByName(name);
            if (consumer != null) { //已存在
                removeConsumer(consumer.getUuid());
                logger.warn("remote consumer '{}' has reg, remove old", name);
            }
            CrawlerClientReceiver consumerClient = new HttpRemoteConsumer(host);
            consumer = new CrawlerUser(name, consumerClient);
            consumer.start();
            logger.info("reg remote consumer : {}", consumer);
            consumerMap.put(consumer.getUuid(), consumer);
            return consumer.getUuid();
        }
    }

    @Override
    public List<TaskStatistic> getRunningTasks(String uuid) {
        return getConsumerByUUID(uuid).getRunningTasks();
    }

    @Override
    public CrawlerUserInfo getResourceInfo(String uuid) {
        CrawlerUser user = this.getConsumerByUUID(uuid);
        CrawlerUserInfo crawlerUserInfo = user.getResourceInfo();
        crawlerUserInfo.setCurWait(taskQueue.getWaitSize(user.getName()));
        return crawlerUserInfo;
    }

    @Override
    public void addTask(String uuid, TaskInfo taskInfo) {
        this.getConsumerByUUID(uuid).addTask(taskInfo);
    }

    public SpiderRegulate.SpiderInfo getTaskSummary(String uuid) {
        return getConsumerByUUID(uuid).getTaskManager().getSpiderInfo();
    }


    @Scheduled(fixedRate = 30 * 1000, initialDelay = 30 * 1000)
    public void schedule() {
        logger.info("schedule task");
        for (CrawlerUser consumer : this.getAllConsumer()) {
            int canRunNum = consumer.getTaskManager().getMaxRunning() - consumer.getTaskManager().getCurRunningSize();
            for (int i = 0; i < canRunNum; i++) {
                String temp = taskQueue.pop(consumer.getName());
                if (temp != null) {
                    TaskInfo taskInfo = Json.toObject(temp, TaskInfo.class);
                    consumer.getTaskManager().addTask(taskInfo);
                    logger.info("consumer : {} run task : {}", consumer.getName(), taskInfo.getId());
                } else {
                    logger.info("consumer : {} has no task", consumer.getName());
                    break;
                }
            }
        }
    }
}
