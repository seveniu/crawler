package com.seveniu;

import com.seveniu.user.CrawlerUser;
import com.seveniu.user.UserManager;
import com.seveniu.spider.SpiderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by seveniu on 5/12/16.
 * TaskManager
 */
@Component
public class SpiderRegulate {
    private static final int THREAD_THRESHOLD = 200;
    private Logger logger = LoggerFactory.getLogger(SpiderRegulate.class);
    @Autowired
    UserManager userManager;

    private LinkedBlockingQueue<SpiderTask> waitTaskQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<SpiderTask> runningTask = new LinkedBlockingQueue<>();
    private AtomicInteger curThread = new AtomicInteger(0);


    public void start() {
        checkTaskStatus();
    }

    public List<SpiderInfo> consumerSpiderInfo() {
        Collection<CrawlerUser> consumers = userManager.getAllConsumer();
        List<SpiderInfo> spiderInfoList = new ArrayList<>(consumers.size());
        for (CrawlerUser consumer : consumers) {

            spiderInfoList.add(consumer.getTaskManager().getSpiderInfo());
        }
        spiderInfoList.addAll(consumers.stream().map(consumer -> consumer.getTaskManager().getSpiderInfo()).collect(Collectors.toList()));
        return spiderInfoList;
    }

    public static class SpiderInfo {
        private String consumerName;
        private int waitSpiderNum;
        private int runningSpiderNum;
        private int runThreadNum;

        public String getConsumerName() {
            return consumerName;
        }

        public void setConsumerName(String consumerName) {
            this.consumerName = consumerName;
        }

        public int getWaitSpiderNum() {
            return waitSpiderNum;
        }

        public void setWaitSpiderNum(int waitSpiderNum) {
            this.waitSpiderNum = waitSpiderNum;
        }

        public int getRunningSpiderNum() {
            return runningSpiderNum;
        }

        public void setRunningSpiderNum(int runningSpiderNum) {
            this.runningSpiderNum = runningSpiderNum;
        }

        public int getRunThreadNum() {
            return runThreadNum;
        }

        public void setRunThreadNum(int runThreadNum) {
            this.runThreadNum = runThreadNum;
        }
    }


    private void checkTaskStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Iterator<SpiderTask> iterator = runningTask.iterator();
                        while (iterator.hasNext()) {
                            SpiderTask task = iterator.next();
                            if (task.getStatus() == Spider.Status.Stopped) {
                                iterator.remove();
                                curThread.addAndGet(0 - task.taskInfo().getThreadNum());
                            }
                        }
                        TimeUnit.SECONDS.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("check thread is error : {}", e.getMessage());
                        break;
                    }
                }
            }
        }, "taskManager-check-task-thread").start();
    }


}
