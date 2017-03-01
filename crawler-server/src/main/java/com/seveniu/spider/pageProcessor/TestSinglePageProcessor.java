package com.seveniu.spider.pageProcessor;

import com.seveniu.user.CrawlerUser;
import com.seveniu.entity.data.Node;
import com.seveniu.entity.data.PageResult;
import com.seveniu.spider.parse.ParseResult;
import com.seveniu.template.PagesTemplate;
import com.seveniu.template.def.Field;
import com.seveniu.template.def.FieldType;
import com.seveniu.template.def.PageTemplate;
import us.codecraft.webmagic.Page;

import static com.seveniu.def.PageContext.CONTEXT_NODE;

/**
 * Created by seveniu on 7/5/16.
 * TestSinglePageProcessor
 * 测试用,只解析一页,链接不跳转
 */
public class TestSinglePageProcessor extends MyPageProcessor {


    public TestSinglePageProcessor(PagesTemplate pagesTemplate, CrawlerUser consumer) {
        super(pagesTemplate, consumer);
    }

    @Override
    void process0(Page page, ParseResult parseResult) {
        singlePage(page, parseResult);
    }

    @Override
    protected PageTemplate getTemplate(Page page) {
        // 根据序号找到对应模板
        PageTemplate pageTemplate = pagesTemplate.getTemplate(0);
        if (pageTemplate == null) {
            logger.error("can't find template by serial num : {}", 0);
            return null;
        }
        for (Field field : pageTemplate.getFields()) {
            if (field.getType() == FieldType.TARGET_LINK.getId()
                    || field.getType() == FieldType.NEXT_LINK.getId()) {
                field.setType(FieldType.TEXT_LINK.getId());
            }
        }
        return pageTemplate;
    }


    private void singlePage(Page page, ParseResult parseResult) {
        String url = page.getUrl().get();
        // 处理解析的结果
        Node contextNode = (Node) page.getRequest().getExtra(CONTEXT_NODE);
        // 内容
        if (parseResult.getFieldResults() != null && parseResult.getFieldResults().size() > 0) {
            // 有文本字段,就获取 node 添加内容
            if (contextNode == null) {
                contextNode = new Node(url, taskId);
                statistic.addCreateNodeCount(1);
            }
            contextNode.addPageResult(new PageResult().setUrl(url).setFieldResults(parseResult.getFieldResults()));
        } else {
            logger.error("content page field is null, url : {}", url);
        }

    }

}
