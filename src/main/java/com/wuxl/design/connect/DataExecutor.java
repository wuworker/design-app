package com.wuxl.design.connect;

import android.util.Log;

import com.wuxl.design.connect.impl.DefaultDataExecutor;
import com.wuxl.design.connect.protocol.DataPackage;

import java.util.Arrays;

/**
 * Created by wuxingle on 2017/4/12 0012.
 * 数据接收解析和发送封装
 */
public abstract class DataExecutor {

    private static final String TAG = "DataExecutor";

    protected DataPackage dataPackage;

    private TCPConnector connector;

    private byte[] origin;

    public DataExecutor(){
        dataPackage = new DataPackage();
    }

    /**
     * 拿到默认的数据解析器
     * @param connector 连接
     * @return executor
     */
    public static DataExecutor getDefaultDataExecutor(TCPConnector connector){
        DataExecutor dataExecutor = new DefaultDataExecutor();
        dataExecutor.setConnector(connector);
        return dataExecutor;
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

    public void sendData(byte[] target,byte cmd,int... data){
        dataPackage.setTarget(target);
        byte[] datas = dataPackage.getData();
        for(int i=0;i<datas.length;i++){
            datas[i] = (byte)data[i];
        }
        dataPackage.setDataLen(data.length);
        sendData(cmd);
    }

    /**
     * 无数据发送
     */
    public void sendData(byte[] target,byte cmd){
        dataPackage.setTarget(target);
        sendData(cmd);
    }

    /**
     * 默认往上次地址发送
     * @param cmd cmd
     * @param data data
     */
    public void sendData(byte cmd,int data){
        dataPackage.getData()[0]=(byte)data;
        dataPackage.setDataLen(1);
        sendData(cmd);
    }

    /**
     * 发送无数据命令
     */
    public void sendData(byte cmd){
        dataPackage.setCmd(cmd);
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
        //清空数据
        dataPackage.clear();
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

    public TCPConnector getConnector() {
        return connector;
    }

    public void setConnector(TCPConnector connector) {
        this.connector = connector;
    }
}
