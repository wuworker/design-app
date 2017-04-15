package com.wuxl.design.model;

import android.util.Log;

import com.wuxl.design.connect.DataExecutor;

import static com.wuxl.design.connect.protocol.DataProtocol.*;

/**
 * Created by wuxingle on 2017/4/15 0015.
 * wifi设备的管理器
 */
public class WifiDeviceManager {

    private static final String TAG = "WifiDeviceManager";

    private DataExecutor dataExecutor;

    public static WifiDeviceManager instance;

    private WifiDeviceManager(DataExecutor dataExecutor) {
        this.dataExecutor = dataExecutor;
    }

    public static WifiDeviceManager getInstance(DataExecutor dataExecutor) {
        if (instance == null) {
            instance = new WifiDeviceManager(dataExecutor);
        }
        return instance;
    }

    /**
     * 打开设备
     * @param wifiDevice 设备
     */
    public void on(WifiDevice wifiDevice){
        dataExecutor.sendData(wifiDevice.getId(),CMD_ON,100);
    }

    /**
     * 关闭设备
     * @param wifiDevice 设备
     */
    public void off(WifiDevice wifiDevice){
        dataExecutor.sendData(wifiDevice.getId(),CMD_OFF,0);
    }

    /**
     * 调整设备亮度
     * @param wifiDevice 设备
     * @param percent 百分比
     */
    public void setPwm(WifiDevice wifiDevice,int percent){
        if(percent<0 || percent>100){
            Log.w(TAG,"pwm的百分比不合法:"+percent);
        }
        dataExecutor.sendData(wifiDevice.getId(),CMD_PWM,percent);
    }








}
