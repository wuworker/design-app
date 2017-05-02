package com.wuxl.design.wifidevice;

import android.util.Log;

import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataCmdSender;

import java.util.Arrays;

import static com.wuxl.design.connect.protocol.DataProtocol.IS_APP;
import static com.wuxl.design.connect.protocol.DataProtocol.OFF;
import static com.wuxl.design.connect.protocol.DataProtocol.ON;
import static com.wuxl.design.connect.protocol.DataProtocol.ONLINE;
import static com.wuxl.design.connect.protocol.DataProtocol.PWM;

/**
 * Created by wuxingle on 2017/5/2 0002.
 * 设备命令的实现
 */
public class WifiDeviceCmdSender implements DataCmdSender {

    private static final String TAG = "WifiDeviceCmdSender";

    private DataExecutor dataExecutor;

    private WifiDeviceConnectManager connectManager;

    public WifiDeviceCmdSender(WifiDeviceConnectManager connectManager, DataExecutor dataExecutor){
        this.dataExecutor = dataExecutor;
        this.connectManager = connectManager;
    }


    /**
     * 打开设备
     */
    @Override
    public void on(WifiDevice device) {
        if(connectManager.isReady()){
            Log.i(TAG,"打开设备");
            dataExecutor.sendData(device.getId(),ON);
        }else {
            Log.w(TAG,"service未启动");
        }
    }


    /**
     * 关闭设备
     */
    @Override
    public void off(WifiDevice device) {
        if(connectManager.isReady()){
            Log.i(TAG,"关闭设备");
            dataExecutor.sendData(device.getId(),OFF);
        }else {
            Log.w(TAG,"service未启动");
        }
    }


    /**
     * 设备调光
     */
    @Override
    public void setPwm(WifiDevice device, int pwm) {
        if(connectManager.isReady()){
            if(pwm<0 || pwm>100){
                Log.w(TAG,"pwm的百分比不合法:"+pwm);
            }
            Log.i(TAG,"设置设备pwm:"+pwm);
            dataExecutor.sendData(device.getId(),PWM,pwm);
        }else {
            Log.w(TAG,"service未启动");
        }
    }


    /**
     * 判断设备是否在线
     */
    @Override
    public void isOnline(WifiDevice device) {
        if(connectManager.isReady()){
            Log.i(TAG,"发送数据,判断设备是否在线,"+ Arrays.toString(device.getId()));
            dataExecutor.sendData(device.getId(),ONLINE);
        }else {
            Log.w(TAG,"service未启动");
        }
    }


    /**
     * 向服务器注册
     */
    @Override
    public void register() {
        if(connectManager.isReady()){
            Log.i(TAG,"向服务器注册");
            dataExecutor.sendData(IS_APP);
        }else {
            Log.w(TAG,"service未启动");
        }
    }
}
