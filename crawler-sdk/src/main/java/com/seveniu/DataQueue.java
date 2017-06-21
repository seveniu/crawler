package com.seveniu;

import com.seveniu.entity.data.Node;
import com.seveniu.service.CrawlerClientReceiver;
import com.seveniu.util.DBUtil;
import com.seveniu.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by seveniu on 10/11/16.
 * *
 */
@Service
public class DataQueue {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String PREFIX = "data-";
    private String key;
    private CrawlerClientReceiver crawlerClientReceiver;
    private int threadNum = 20;
    @Value("${crawler.data.url}")
    String url;
    @Value("${crawler.data.username}")
    String username;
    @Value("${crawler.data.password}")
    String password;
    @Value("${crawler.data.emptySleepTime:60}")
    int emptySleepTime;

    @Autowired
    public DataQueue(@Value("${crawler.name}") String name, CrawlerClientReceiver crawlerClientReceiver) {
        this.key = name;
        this.crawlerClientReceiver = crawlerClientReceiver;
    }

    private ThreadPoolExecutor threadPoolExecutor;

    public void start() {
        init();
        try {
            DBUtil.openConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        int finalSleepTime = emptySleepTime;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int lastId = 0;
                while (true) {
                    try {
                        if (threadPoolExecutor.getActiveCount() >= threadPoolExecutor.getMaximumPoolSize()) {
                            TimeUnit.SECONDS.sleep(10);
                            logger.info("executor is all active");
                            continue;
                        }
                        List<Map<String, Object>> mapList = DBUtil.queryMapList("select id,`data` from queue where `name` = ? and id > ? limit ?", key, lastId, threadNum);
                        if (mapList == null) {
                            throw new RuntimeException("query data return null");
                        }
                        if (mapList.size() == 0) {
                            TimeUnit.SECONDS.sleep(finalSleepTime);
                            logger.info("queue : {} is empty", key);
                            continue;
                        }
//                        Map<String, Object> map = DBUtil.queryMap("select id,`data` from queue where `name` = ? limit 1", key);
//                        if (map == null) {
//                            TimeUnit.SECONDS.sleep(10);
//                            logger.info("queue : {} is empty", key);
//                            continue;
//                        }
                        for (Map<String, Object> map : mapList) {

                            int id = ((Long) map.get("id")).intValue();
                            String data = (String) map.get("data");
                            threadPoolExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        crawlerClientReceiver.done(Json.toObject(data, Node.class));
                                        DBUtil.update("delete from queue where id =?", id);
                                        TimeUnit.SECONDS.sleep(1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        logger.error("data queue process data id: {} error", id, e);
                                    }
                                }
                            });
                            lastId = id;
                        }
                    } catch (Exception e) {
                        logger.error("consumer data error : {}", e.getMessage());
                        e.printStackTrace();
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }, "get-data-from-queue-thread").start();
    }

    private void init() {
        if (threadPoolExecutor == null) {
            this.threadPoolExecutor = new ThreadPoolExecutor(
                    0, threadNum, 10, TimeUnit.MINUTES, new SynchronousQueue<>(),
                    new ThreadFactory() {
                        AtomicInteger count = new AtomicInteger();

                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "data-process-thread-" + count.getAndIncrement());
                        }
                    },
                    new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            if (!executor.isShutdown()) {
                                try {
                                    executor.getQueue().put(r);
                                } catch (InterruptedException e) {
                                    // should not be interrupted
                                }
                            }
                        }
                    }
            );

        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setCrawlerClientReceiver(CrawlerClientReceiver crawlerClientReceiver) {
        this.crawlerClientReceiver = crawlerClientReceiver;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
