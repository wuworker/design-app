package com.wuxl.design.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wuxl.design.R;
import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataPackage;
import com.wuxl.design.service.ConnectBinder;
import com.wuxl.design.service.TCPConnectService;

import java.util.Arrays;

import static com.wuxl.design.utils.AppUtils.setStatusBarColor;

/**
 * 首页
 * Created by wuxingle on 2017/4/10 0010.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DataExecutor dataExecutor;

    private String ip;

    private int port;

    private byte[] origin;

    private byte[] empty = new byte[6];

    private ServiceConnection connection = new ServiceConnection() {
        private ConnectBinder binder;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ConnectBinder)service;
            binder.setConnectorListener(listener);
            dataExecutor = binder.getDataExecutor();
            dataExecutor.setOrigin(origin);
            dataExecutor.setTarget(empty);
            binder.connect(ip,port);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG,"service bind fail");
        }
    };

    private ConnectorListener listener = new ConnectorListener() {
        @Override
        public void connectResult(boolean success) {
            Log.i(TAG,"连接:"+success);
            if(success){
                //自我设置
                dataExecutor.sendData((byte)12,1000);
                goNext();
            }
            goNext();
        }

        @Override
        public void arrivedMessage(byte[] bytes) {
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

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this,Color.rgb(0x87,0xce,0xeb));
        setContentView(R.layout.activity_main);

        init();

        Intent intent = new Intent(this, TCPConnectService.class);
        startService(intent);
        bindService(intent,connection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 初始化
     */
    private void init(){
        ip = getResources().getString(R.string.server_ip);
        port = Integer.parseInt(getResources().getString(R.string.server_port));
        origin = new byte[6];
        Arrays.fill(origin,(byte)0x23);
    }

    /**
     * go next activity
     */
    private void goNext(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
        Log.i(TAG,"main go next");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(connection);
        Log.i(TAG,"main activity destroy");
    }


}
