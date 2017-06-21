package com.seveniu.entity.task;

public class ConsumerConfig {

    public String name; // required
    public String type; // required
    public String duplicateUrl; // optional
    public String doneUrl; // optional
    public String statisticUrl; // optional
    public String taskUrl; // optional
    public String host; // optional
    public int port; // optional

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuplicateUrl() {
        return duplicateUrl;
    }

    public void setDuplicateUrl(String duplicateUrl) {
        this.duplicateUrl = duplicateUrl;
    }

    public String getDoneUrl() {
        return doneUrl;
    }

    public void setDoneUrl(String doneUrl) {
        this.doneUrl = doneUrl;
    }

    public String getStatisticUrl() {
        return statisticUrl;
    }

    public void setStatisticUrl(String statisticUrl) {
        this.statisticUrl = statisticUrl;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

