package com.wuxl.design.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.impl.TCPConnectorImpl;

/**
 * service组件
 * Created by wuxingle on 2017/4/12 0012.
 */
public class TCPConnectService extends Service{

    private static final String TAG = "TCPConnectService";

    private ConnectBinder binder;

    private TCPConnector connector;

    @Override
    public void onCreate() {
        super.onCreate();
        connector = new TCPConnectorImpl();
        binder = new ConnectBinder(connector);
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
        super.onDestroy();
        Log.i(TAG,"service已关闭");
    }
}
