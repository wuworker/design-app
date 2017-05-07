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
     * 获取设备信息
     */
    void getStatus(WifiDevice device);

    /**
     * 取消定时
     */
    void clearTime(WifiDevice device);

    /**
     * 定时开
     */
    void onTime(WifiDevice device,int day,int hour,int minute,int second);

    /**
     * 定时关
     */
    void offTime(WifiDevice device,int day,int hour,int minute,int second);

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
