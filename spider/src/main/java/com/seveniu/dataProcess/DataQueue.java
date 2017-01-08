package com.seveniu.dataProcess;


import com.seveniu.entity.data.Node;

/**
 * Created by seveniu on 10/24/16.
 * *
 */
public interface DataQueue {
    void addData(String key, Node node);
}
