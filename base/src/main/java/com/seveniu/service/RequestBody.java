package com.seveniu.service;

/**
 * Created by seveniu on 1/15/17.
 * *
 */
public class RequestBody {
    private String action;
    private String data;

    public RequestBody() {
    }

    public RequestBody(String action, String data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

