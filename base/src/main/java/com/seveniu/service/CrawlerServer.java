package com.seveniu.service;

import com.seveniu.entity.task.ConsumerConfig;

/**
 * Created by seveniu on 1/8/17.
 * *
 */
public interface CrawlerServer {
    String reg(ConsumerConfig consumerConfig) throws Exception;
}
