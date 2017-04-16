package com.wuxl.design.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.impl.TCPConnectorImpl;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * service组件
 * Created by wuxingle on 2017/4/12 0012.
 */
public class TCPConnectService extends Service{

    private static final String TAG = "TCPConnectService";

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
        //设备mac
        byte[] origin = new byte[6];
        Arrays.fill(origin,(byte)0x23);

        connector = new TCPConnectorImpl();
        binder = new LocalBinder();
        dataExecutor = DataExecutor.getDefaultDataExecutor(connector);
        dataExecutor.setOrigin(origin);
        dataExecutor.setTarget(new byte[6]);

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
        super.onDestroy();
        Log.i(TAG,"service已关闭");
    }

    /**
     * 启动
     */
    public void connect(final String ip,final int port){
        if(connector.isConnect()){
            Log.i(TAG,"tcp已在连接");
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                connector.connect(ip,port);
            }
        });
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
