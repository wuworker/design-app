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
import com.wuxl.design.connect.protocol.DataPackage;
import com.wuxl.design.service.ConnectBinder;
import com.wuxl.design.service.TCPConnectService;

import java.io.IOException;
import java.util.Arrays;

import static com.wuxl.design.utils.AppUtils.setStatusBarColor;

/**
 * 首页
 * Created by wuxingle on 2017/4/10 0010.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final byte[] ID1 = new byte[6];
    private static final byte[] ID2 = new byte[6];
    private static final byte[] ID3 = new byte[6];
    private static final byte[] EMPTY = new byte[6];

    static{
        Arrays.fill(ID1,(byte)0x1f);
        Arrays.fill(ID2,(byte)0x23);
        Arrays.fill(ID3,(byte)0x56);
    }

    private ConnectBinder binder;

    private DataPackage dataPackage;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ConnectBinder)service;
            binder.setConnectorListener(listener);
            try {
                binder.connect("192.168.1.100",9999);
            }catch (IOException e){
                Log.e(TAG,"连接异常",e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ConnectorListener listener = new ConnectorListener() {
        @Override
        public void connectResult(boolean success) {
            Log.i(TAG,"连接:"+success+","+dataPackage);
            binder.send(dataPackage);
        }

        @Override
        public void arrivedMessage(byte[] bytes) {
            DataPackage dataPackage = binder.parse(bytes);
            Log.i(TAG,"数据来源:"+dataPackage.getHexOrigin());
            Log.i(TAG,"数据命令:"+ Arrays.toString(dataPackage.getCmd()));
            Log.i(TAG,"数据:"+Arrays.toString(dataPackage.getData()));
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

        dataPackage = new DataPackage();
        dataPackage.setOrigin(ID2);
        dataPackage.setTarget(EMPTY);
        dataPackage.setCmd(new byte[]{0x11});
        dataPackage.setData(new byte[]{0x12,0x13,0x14});

        Intent intent = new Intent(this, TCPConnectService.class);
        //startService(intent);
        bindService(intent,connection, Service.BIND_AUTO_CREATE);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(connection);

    }


}
