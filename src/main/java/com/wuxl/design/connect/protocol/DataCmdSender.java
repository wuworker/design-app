package com.wuxl.design.connect.protocol;

import com.wuxl.design.wifidevice.WifiDevice;

import java.util.List;

/**
 * Created by wuxingle on 2017/5/2 0002.
 * 命令发送接口
 */
public interface DataCmdSender {

    /**
     * 打开设备
     */
    void on(WifiDevice device);

    /**
     * 关闭设备
     */
    void off(WifiDevice device);

    /**
     * 设备调光
     */
    void setPwm(WifiDevice device,int pwm);

    /**
     * 判断设备是否在线
     */
    void isOnline(WifiDevice device);

    /**
     * 添加设备
     */
    void addInterested(List<WifiDevice> devices);

    /**
     * 向服务器注册
     */
    void register();

}
