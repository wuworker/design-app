package com.wuxl.design.connect.protocol;

import com.wuxl.design.wifidevice.WifiDevice;

/**
 * Created by wuxingle on 2017/5/2 0002.
 * 命令发送接口
 */
public interface DataCmdSender {

    //向单片机发送的
    /**
     * 打开设备
     */
    void on(WifiDevice device,int pwm);

    /**
     * 关闭设备
     */
    void off(WifiDevice device);

    /**
     * 判断设备是否在线
     */
    void isOnline(WifiDevice device);

    //
    void clearTime(WifiDevice device);

    void onTime(WifiDevice device,int minute);

    void offTime(WifiDevice device,int minute);

    //向服务器发送的
    /**
     * 添加设备
     */
    void addInterested(WifiDevice devices);

    /**
     * 向服务器注册
     */
    void register();

}
