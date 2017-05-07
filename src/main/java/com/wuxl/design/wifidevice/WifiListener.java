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
     * 设备状态改变
     */
    void changeStatus(String hexId,int level,boolean timeOver);


    /**
     * 服务器发送
     * 设备上下线通知
     */
    void isOnline(String hexId, boolean status);







}
