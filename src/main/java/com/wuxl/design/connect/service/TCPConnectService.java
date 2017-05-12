package com.wuxl.design.connect.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wuxl.design.common.utils.AppUtils;
import com.wuxl.design.common.utils.DataUtils;
import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.impl.TCPConnectorImpl;

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * service组件
 * Created by wuxingle on 2017/4/12 0012.
 */
public class TCPConnectService extends Service{

    private static final String TAG = "TCPConnectService";

    //一次建立一条连接
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    private TCPConnector connector;

    private DataExecutor dataExecutor;

    private LocalBinder binder;

    public class LocalBinder extends Binder{

        public TCPConnectService getService(){
            return TCPConnectService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String mac=null;
        //设备mac
        try {
            mac = AppUtils.getMacAdress(this);
        }catch (SocketException e){
            Log.e(TAG,"get mac error");
        }

        byte[] origin = DataUtils.toByte(mac);

        Log.i(TAG,"获取的MAC为:"+DataUtils.toHex(origin));

        connector = new TCPConnectorImpl();
        binder = new LocalBinder();
        dataExecutor = DataExecutor.getDefaultDataExecutor(connector);
        dataExecutor.setOrigin(origin);

        Log.i(TAG,"service启动");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"service绑定");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"service解绑");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        connector.close();
        threadPool.shutdown();
        Log.i(TAG,"service已关闭");
        super.onDestroy();
        //不用下面这个，记得释放static变量
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 启动
     */
    public void connect(final String ip,final int port){

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                connector.connect(ip,port);
            }
        });
    }

    /**
     * 是否在连接
     */
    public boolean isConnecting(){
        return connector.isConnecting();
    }

    /**
     * 连接是否可用
     */
    public boolean isConnectable(){
        return connector.isConnectable();
    }

    /**
     * 设置监听
     * @param listener listener
     */
    public void setConnectListener(ConnectorListener listener){
        this.connector.setListener(listener);
    }

    /**
     * 这个类用于收发数据
     * @return 数据解析器
     */
    public DataExecutor getDataExecutor(){
        return dataExecutor;
    }

}
