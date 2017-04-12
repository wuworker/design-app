package com.wuxl.design.connect;

/**
 * 连接者的监听器
 * Created by wuxingle on 2017/4/12 0012.
 */
public interface ConnectorListener {

    void connectResult(boolean success);

    void arrivedMessage(byte[] bytes);

    void sendComplete(byte[] bytes);

    void connectLost(String msg);

}
