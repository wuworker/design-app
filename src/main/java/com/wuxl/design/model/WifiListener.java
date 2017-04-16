package com.wuxl.design.model;

/**
 * Created by wuxingle on 2017/4/16 0016.
 *
 */
public interface WifiListener {

    /**
     * 可以开始连接
     */
    void canConnect();

    /**
     * 在线通知
     * @param hexId 设备id
     */
    void isOnline(String hexId);









}
