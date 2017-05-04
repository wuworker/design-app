package com.wuxl.design.wifidevice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataCmdSender;
import com.wuxl.design.connect.protocol.DataPackage;
import com.wuxl.design.connect.service.TCPConnectService;

import java.util.Arrays;

import static com.wuxl.design.connect.protocol.DataProtocol.DOWNING;
import static com.wuxl.design.connect.protocol.DataProtocol.OK;
import static com.wuxl.design.connect.protocol.DataProtocol.UPING;

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

    private DataCmdSender cmdSender;

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
            cmdSender = new WifiDeviceCmdSender(WifiDeviceConnectManager.this,dataExecutor);
            if(wifiListener!=null){
                wifiListener.canConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    /**
     * 网络连接监听
     */
    private ConnectorListener connectListener = new ConnectorListener() {
        @Override
        public void connectResult(boolean success) {
            if(success){
                Log.i(TAG,"连接成功，进行注册");
                cmdSender.register();
                if(wifiListener!=null){
                    wifiListener.connectResult(true);
                }
            }else {
                wifiListener.connectResult(false);
            }
        }

        @Override
        public void arrivedMessage(byte[] bytes) {
            Log.i(TAG,"收到数据");
            DataPackage dataPackage = dataExecutor.toDataPackage(bytes);
            if(dataPackage == null){
                Log.i(TAG,"解析失败,忽略这次数据");
                return;
            }
            if(wifiListener == null){
                Log.i(TAG,"未设置wifi的监听");
                return;
            }
            switch (dataPackage.getCmd()){
                case OK:
                    Log.i(TAG,"设备"+dataPackage.getHexOrigin()+"存在");
                    wifiListener.isOnline(dataPackage.getHexOrigin(),dataPackage.getData()[0]);
                    break;
                case DOWNING:
                    Log.i(TAG,"设备掉线");
                    wifiListener.changeStatus(dataPackage.getHexOrigin(),false);
                    break;
                case UPING:
                    Log.i(TAG,"设备上线");
                    wifiListener.changeStatus(dataPackage.getHexOrigin(),true);
                    break;
                default:
                    Log.i(TAG,"其他命令");
                    break;
            }

            Log.d(TAG,"收到数据origin:"+dataPackage.getHexOrigin());
            Log.d(TAG,"收到数据cmd:"+dataPackage.getCmd());
            Log.d(TAG,"收到数据data:"+Arrays.toString(dataPackage.getData()));
        }

        @Override
        public void sendComplete(byte[] bytes) {
            Log.i(TAG,"发送回调");
        }

        @Override
        public void connectLost(String msg) {
            Log.i(TAG,"与服务器断开连接");
            if(wifiListener!=null){
                wifiListener.connectResult(false);
            }
        }
    };

    private WifiDeviceConnectManager(){}

    /**
     * 获得单例
     * @return manager
     */
    public static WifiDeviceConnectManager getInstance(){
        if(connectManager == null){
            connectManager = new WifiDeviceConnectManager();
        }
        return connectManager;
    }

    /**
     * 开启service服务
     */
    public void ready(Context context){
        this.context = context;
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
     * 获得数据发送接口
     * 只有service启动后才能获取
     */
    public DataCmdSender getCmdSender(){
        if(cmdSender == null){
            throw new RuntimeException("service未启动，不能获取");
        }
        return cmdSender;
    }

    /**
     * 是否正在连接
     */
    public boolean isConnecting(){
        return service.isConnecting();
    }

    /**
     * 连接是否可用
     */
    public boolean isConnectable(){
        return service.isConnectable();
    }

    /**
     * 准备就绪的标准是service已启动
     * @return isReady
     */
    public boolean isReady(){
        return service!=null && dataExecutor!=null;
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
        service = null;
        dataExecutor = null;
        connectManager = null;
    }

    /**
     * wifi管理连接监听
     * @param listener listener
     */
    public void setListener(WifiListener listener){
        this.wifiListener = listener;
    }


}
