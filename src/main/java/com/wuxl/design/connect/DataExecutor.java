package com.wuxl.design.connect;

import android.util.Log;

import com.wuxl.design.connect.protocol.DataPackage;

/**
 * 数据接收解析和发送封装
 * Created by wuxingle on 2017/4/12 0012.
 */
public abstract class DataExecutor {

    private static final String TAG = "DataExecutor";

    protected TCPConnector connector;

    public DataExecutor(TCPConnector connector){
        this.connector = connector;
    }

    /**
     * 数据封装
     * @param bytes data
     * @return package
     */
    public abstract DataPackage toDataPackage(byte[] bytes);

    /**
     * 数据拆包
     * @param dataPackage package
     * @return data
     */
    public abstract byte[] fromDataPackage(DataPackage dataPackage);


    /**
     * 发送数据
     * @param dataPackage data
     */
    public void sendData(DataPackage dataPackage){
        byte[] data = fromDataPackage(dataPackage);
        if(connector!=null){
            connector.sendData(data);
        } else {
            Log.w(TAG,"没有连接");
        }
    }

    /**
     * 读取数据
     * @param bytes data
     * @return package
     */
    public DataPackage readData(byte[] bytes){
        return toDataPackage(bytes);
    }


}
