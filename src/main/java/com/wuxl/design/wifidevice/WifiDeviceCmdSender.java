package com.wuxl.design.wifidevice;

import android.util.Log;

import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataCmdSender;

import java.util.Arrays;


import static com.wuxl.design.connect.protocol.DataProtocol.*;

/**
 * Created by wuxingle on 2017/5/2 0002.
 * 设备命令的实现
 */
public class WifiDeviceCmdSender implements DataCmdSender {

    private static final String TAG = "WifiDeviceCmdSender";

    private DataExecutor dataExecutor;

    private WifiDeviceConnectManager connectManager;

    public WifiDeviceCmdSender(WifiDeviceConnectManager connectManager, DataExecutor dataExecutor) {
        this.dataExecutor = dataExecutor;
        this.connectManager = connectManager;
    }


    /**
     * 打开设备
     */
    @Override
    public void on(WifiDevice device, int pwm) {
        if (connectManager.isReady()) {
            Log.i(TAG, "打开设备");
            dataExecutor.sendData(device.getId(), ON, pwm);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 关闭设备
     */
    @Override
    public void off(WifiDevice device) {
        if (connectManager.isReady()) {
            Log.i(TAG, "关闭设备");
            dataExecutor.sendData(device.getId(), OFF);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 判断设备是否在线
     */
    @Override
    public void getStatus(WifiDevice device) {
        if (connectManager.isReady()) {
            Log.i(TAG, "发送数据,判断设备是否在线," + Arrays.toString(device.getId()));
            dataExecutor.sendData(device.getId(), STATUS);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 取消定时
     */
    @Override
    public void clearTime(WifiDevice device) {
        if (connectManager.isReady()) {
            Log.i(TAG, "发送数据,清除定时任务" + Arrays.toString(device.getId()));
            dataExecutor.sendData(device.getId(), TIME_CLR);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 定时开
     */
    @Override
    public void onTime(WifiDevice device, int day,int hour,int minute,int second) {
        if (connectManager.isReady()) {
            Log.i(TAG, "发送数据,设置定时开" + Arrays.toString(device.getId()));
            dataExecutor.sendData(device.getId(), TIME_ON,
                    device.getTimePwm() , day,hour,minute,second);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 定时关
     */
    @Override
    public void offTime(WifiDevice device,int day,int hour,int minute,int second) {
        if (connectManager.isReady()) {
            Log.i(TAG, "发送数据,设置定时关");
            dataExecutor.sendData(device.getId(), TIME_OFF, day,hour,minute,second);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 添加设备
     */
    @Override
    public void addInterested(WifiDevice device) {
        if (connectManager.isReady()) {
            Log.i(TAG, "添加感兴趣的设备列表");
            dataExecutor.sendData(device.getId(), ADD_LED);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

    /**
     * 向服务器注册
     */
    @Override
    public void register() {
        if (connectManager.isReady()) {
            Log.i(TAG, "向服务器注册");
            dataExecutor.sendData(IS_APP);
        } else {
            Log.w(TAG, "service未启动");
        }
    }

}
