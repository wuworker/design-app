package com.wuxl.design.model;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataPackage;
import com.wuxl.design.service.TCPConnectService;

import static com.wuxl.design.connect.protocol.DataProtocol.CMD_OFF;
import static com.wuxl.design.connect.protocol.DataProtocol.CMD_ON;
import static com.wuxl.design.connect.protocol.DataProtocol.CMD_PWM;

/**
 * Created by wuxingle on 2017/4/16 0016.
 * wifi设备的连接管理
 */
public class WifiDeviceConnectManager {

    private static final String TAG = "WifiDeviceConnectManage";

    private Context context;

    private static WifiDeviceConnectManager connectManager;

    private DataExecutor dataExecutor;

    private TCPConnectService service;

    private WifiListener wifiListener;

    /**
     * android service连接
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TCPConnectService.LocalBinder binder = (TCPConnectService.LocalBinder)service;
            WifiDeviceConnectManager.this.service = binder.getService();
            WifiDeviceConnectManager.this.service.setConnectListener(connectListener);
            dataExecutor = WifiDeviceConnectManager.this.service.getDataExecutor();
            if(wifiListener!=null){
                wifiListener.canConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 网络连接监听
     */
    private ConnectorListener connectListener = new ConnectorListener() {
        @Override
        public void connectResult(boolean success) {
            Log.i(TAG,"连接"+success);
            dataExecutor.sendData(1,1000);
        }

        @Override
        public void arrivedMessage(byte[] bytes) {
            Log.i(TAG,"收到数据");
            DataPackage dataPackage = dataExecutor.toDataPackage(bytes);
            Log.d(TAG,"收到数据origin:"+dataPackage.getHexOrigin());
            Log.d(TAG,"收到数据cmd:"+dataPackage.getCmd());
            Log.d(TAG,"收到数据data:"+dataPackage.getData());
        }

        @Override
        public void sendComplete(byte[] bytes) {
            Log.i(TAG,"发送回调");
        }

        @Override
        public void connectLost(String msg) {
            Log.i(TAG,"与服务器断开连接");
        }
    };

    private WifiDeviceConnectManager(Context context){
        this.context = context;
    }

    /**
     * 获得单例
     * @param context context
     * @return manager
     */
    public static WifiDeviceConnectManager getInstance(Context context){
        if(connectManager == null){
            connectManager = new WifiDeviceConnectManager(context);
        }
        return connectManager;
    }

    /**
     * 开启service服务
     */
    public void ready(){
        if(isReady()){
            Log.w(TAG,"连接已就绪");
            return;
        }
        Intent intent = new Intent(context,TCPConnectService.class);
        context.startService(intent);
        context.bindService(intent,serviceConnection, Service.BIND_AUTO_CREATE);
        Log.i(TAG,"准备就绪");
    }

    /**
     * 连接
     * @param ip ip
     * @param port port
     */
    public void connect(String ip,int port){
        if(!isReady()){
            Log.w(TAG,"android service 未启动");
            return;
        }
        service.connect(ip,port);
    }

    /**
     * 打开设备
     * @param wifiDevice 设备
     */
    public void on(WifiDevice wifiDevice){
        if(isReady()){
            Log.i(TAG,"打开设备");
            dataExecutor.sendData(wifiDevice.getId(),CMD_ON,100);
        }else {
            Log.w(TAG,"service未启动");
        }

    }

    /**
     * 关闭设备
     * @param wifiDevice 设备
     */
    public void off(WifiDevice wifiDevice){
        if(isReady()){
            Log.i(TAG,"关闭设备");
            dataExecutor.sendData(wifiDevice.getId(),CMD_OFF,0);
        }else {
            Log.w(TAG,"service未启动");
        }
    }

    /**
     * 调整设备亮度
     * @param wifiDevice 设备
     * @param percent 百分比
     */
    public void setPwm(WifiDevice wifiDevice,int percent){
        if(isReady()){
            if(percent<0 || percent>100){
                Log.w(TAG,"pwm的百分比不合法:"+percent);
            }
            Log.i(TAG,"设置设备pwm:"+percent);
            dataExecutor.sendData(wifiDevice.getId(),CMD_PWM,percent);
        }else {
            Log.w(TAG,"service未启动");
        }
    }

    /**
     * 最后的资源清理
     */
    public void close(){
        if(context!=null){
            context.unbindService(serviceConnection);
            context.stopService(new Intent(context,TCPConnectService.class));
            context = null;
        }
    }

    /**
     * wifi管理连接监听
     * @param listener listener
     */
    public void setListener(WifiListener listener){
        this.wifiListener = listener;
    }

    /**
     * 准备就绪的标准是service已启动
     * @return isReady
     */
    private boolean isReady(){
        return service!=null && dataExecutor!=null;
    }
}
