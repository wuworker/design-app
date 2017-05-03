package com.wuxl.design.wifidevice;

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
     * 连接结果
     */
    void connectResult(boolean result);

    /**
     * 在线通知
     * @param hexId 设备id
     */
    void isOnline(String hexId);


    /**
     * 设备状态改变
     */
    void changeStatus(String hexId,boolean status);







}
