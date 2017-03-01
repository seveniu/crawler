package com.seveniu.spider;

import com.seveniu.def.PageContext;
import com.seveniu.entity.data.Node;
import com.seveniu.task.TaskStatistic;
import us.codecraft.webmagic.Request;

/**
 * Created by seveniu on 1/8/17.
 * *
 */
public class TaskStatisticListener implements DownloaderErrorListener {
    private TaskStatistic taskStatistic;

    public TaskStatisticListener(TaskStatistic taskStatistic) {
        this.taskStatistic = taskStatistic;
    }


    @Override
    public void onTimeOutError(Request request) {
        taskStatistic.addNetErrorUrlCount(new String[]{request.getUrl()});
        Node contextNode = (Node) request.getExtra(PageContext.CONTEXT_NODE);
        if (contextNode != null) {
            taskStatistic.addErrorNodeCount(1);
        }
    }

    @Override
    public void onStatusCodeError(Request request, int statusCode) {
        taskStatistic.addNetErrorUrlCount(new String[]{request.getUrl()});
        Node contextNode = (Node) request.getExtra(PageContext.CONTEXT_NODE);
        if (contextNode != null) {
            taskStatistic.addErrorNodeCount(1);
        }
    }

    @Override
    public void onOtherConnectError(Request request) {
        taskStatistic.addNetErrorUrlCount(new String[]{request.getUrl()});
        Node contextNode = (Node) request.getExtra(PageContext.CONTEXT_NODE);
        if (contextNode != null) {
            taskStatistic.addErrorNodeCount(1);
        }

    }

}
