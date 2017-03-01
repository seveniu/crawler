package com.seveniu.spider.pipeline;

import com.seveniu.user.CrawlerUser;
import com.seveniu.def.PageContext;
import com.seveniu.entity.data.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created with IntelliJ IDEA.
 * User: niu
 * Date: 2014/6/11
 * Time: 12:03
 * Project: dhlz-spider
 */
public class MyPipeLine implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CrawlerUser consumer;

    public MyPipeLine(CrawlerUser consumer) {
        this.consumer = consumer;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        if (resultItems.getAll().size() > 0) {

            Node node = resultItems.get(PageContext.CONTEXT_NODE);
            if (node == null) {
                logger.warn("pipeline get node is null");
                return;
            }

            // out 输出
            consumer.transfer(node);
        }
    }


}
