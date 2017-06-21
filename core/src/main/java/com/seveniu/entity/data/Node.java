package com.seveniu.entity.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seveniu on 5/12/16.
 * Node
 */
public class Node {
    private String url;
    private String taskId;
    private List<PageResult> pages = new ArrayList<>();

    public Node(String url, String taskId) {
        this.url = url;
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public List<PageResult> getPages() {
        return pages;
    }

    public String getTaskId() {
        return taskId;
    }

    public synchronized void addPageResult(PageResult pageResult) {
        pages.add(pageResult);
    }
}
