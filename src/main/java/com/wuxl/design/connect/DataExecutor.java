package com.wuxl.design.connect;

import android.util.Log;

import com.wuxl.design.connect.impl.DataExecutorImpl;
import com.wuxl.design.connect.protocol.DataPackage;

import java.util.Arrays;

/**
 * 数据接收解析和发送封装
 * Created by wuxingle on 2017/4/12 0012.
 */
public abstract class DataExecutor {

    private static final String TAG = "DataExecutor";

    protected DataPackage dataPackage;

    private TCPConnector connector;

    private byte[] origin;

    public DataExecutor(TCPConnector connector){
        this.connector = connector;
        dataPackage = new DataPackage();
    }

    /**
     * 拿到默认的数据解析器
     * @param connector 连接
     * @return executor
     */
    public static DataExecutor getDefaultDataExecutor(TCPConnector connector){
        return new DataExecutorImpl(connector);
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
     * @param target target
     * @param cmd cmd
     * @param data data
     */
    public void sendData(byte[] target,byte cmd,int data){
        dataPackage.setTarget(target);
        sendData(cmd,data);
    }

    /**
     * 默认往上次地址发送
     * @param cmd cmd
     * @param data data
     */
    public void sendData(byte cmd,int data){
        dataPackage.setCmd(cmd);
        dataPackage.setData(data);
        sendData(dataPackage);
    }

    /**
     * 发送数据
     * @param dataPackage data
     */
    public void sendData(DataPackage dataPackage){
        dataPackage.setOrigin(origin);
        byte[] data = fromDataPackage(dataPackage);
        Log.d(TAG,"拆包的数据为:"+ Arrays.toString(data));
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

    /**
     * 设置目标
     * @param target target
     */
    public void setTarget(byte[] target){
        dataPackage.setTarget(target);
    }

    /**
     * 设置来源
     * @param origin origin
     */
    public void setOrigin(byte[] origin){
        this.origin=origin;
        dataPackage.setOrigin(origin);
    }

}
