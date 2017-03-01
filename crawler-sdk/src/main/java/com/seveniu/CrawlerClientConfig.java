package com.seveniu;

import com.seveniu.service.CrawlerClient;
import com.seveniu.servlet.CrawlerApiServlet;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CrawlerClientConfig {
    private final CrawlerClient crawlerClient;

    @Autowired
    public CrawlerClientConfig(CrawlerClient crawlerClient) {
        this.crawlerClient = crawlerClient;
    }

    @Bean
    ServletRegistrationBean consumerServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        CrawlerApiServlet servlet = new CrawlerApiServlet(crawlerClient);
//        reg.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, "false");
//        reg.addInitParameter(StatViewServlet.PARAM_NAME_USERNAME, "druid");
//        reg.addInitParameter(StatViewServlet.PARAM_NAME_PASSWORD, "druid");
        reg.setServlet(servlet);
        reg.addUrlMappings("/crawler/consumer/*");
        return reg;
    }
}
