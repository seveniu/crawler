package com.seveniu;

import com.seveniu.service.CrawlerClientReceiver;
import com.seveniu.service.CrawlerServer;
import com.seveniu.servlet.CrawlerApiServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by seveniu on 1/15/17.
 * *
 */
@Configuration
@ComponentScan
public class CrawlerSDK {
    private final CrawlerClientReceiver crawlerClientReceiver;
    private final DataQueue dataQueue;
    private final CrawlerServer crawlerServer;
    @Value("${crawler.server.host}")
    String host;
    @Value("${crawler.name}")
    String name;

    @Autowired
    public CrawlerSDK(CrawlerClientReceiver crawlerClientReceiver, CrawlerServer crawlerServer, DataQueue dataQueue) {
        this.crawlerClientReceiver = crawlerClientReceiver;
        this.crawlerServer = crawlerServer;
        this.dataQueue = dataQueue;
    }

    public void start() {
        crawlerServer.reg(name, host);
        dataQueue.start();
    }

    @Bean
    ServletRegistrationBean consumerServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        CrawlerApiServlet servlet = new CrawlerApiServlet(crawlerClientReceiver);
//        reg.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "false");
//        reg.addInitParameter(StatViewServlet.PARAM_NAME_USERNAME, "druid");
//        reg.addInitParameter(StatViewServlet.PARAM_NAME_PASSWORD, "druid");
        reg.setServlet(servlet);
        reg.addUrlMappings("/crawler/consumer/*");
        return reg;
    }

}
