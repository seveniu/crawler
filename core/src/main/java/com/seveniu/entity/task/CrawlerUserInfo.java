package com.seveniu.entity.task;

public class CrawlerUserInfo {

    public CrawlerUserInfo() {
    }

    public CrawlerUserInfo(int maxRunning, int maxWait, int curRunning, int curWait) {
        this.maxRunning = maxRunning;
        this.maxWait = maxWait;
        this.curRunning = curRunning;
        this.curWait = curWait;
    }

    public int maxRunning; // required
    public int maxWait; // required
    public int curRunning; // required
    public int curWait; // required

    public int getMaxRunning() {
        return maxRunning;
    }

    public void setMaxRunning(int maxRunning) {
        this.maxRunning = maxRunning;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getCurRunning() {
        return curRunning;
    }

    public void setCurRunning(int curRunning) {
        this.curRunning = curRunning;
    }

    public int getCurWait() {
        return curWait;
    }

    public void setCurWait(int curWait) {
        this.curWait = curWait;
    }
}

