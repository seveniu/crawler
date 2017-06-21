package com.seveniu;

import com.seveniu.service.CrawlerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * Created by seveniu on 1/15/17.
 * *
 */
@SpringBootApplication
public class Demo {
    @Autowired
    private CrawlerSDK crawlerSDK;
    @Value("${crawler.redis.host}")
    String host;
    @Value("${crawler.name}")
    String name;

    public static void main(String[] args) {
        SpringApplication.run(Demo.class, args);
    }

    @PostConstruct
    public void init() {
//        System.out.println(host);
        crawlerSDK.start();
    }
}
