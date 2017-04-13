package com.wuxl.design.connect.impl;

import android.util.Log;

import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.protocol.DataPackage;

import java.util.Arrays;

import static com.wuxl.design.connect.protocol.DataProtocol.CMD_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.DATA_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.ORIGIN_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.RECEIVE_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.SEND_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.TARGET_LENGTH;

/**
 * 数据解析封装的实现
 * Created by wuxingle on 2017/4/13 0013.
 */
public class DataExecutorImpl extends DataExecutor {

    private static final String TAG = "DataExecutorImpl";

    private DataPackage dataPackage;

    public DataExecutorImpl(TCPConnector tcpConnector) {
        super(tcpConnector);
        dataPackage = new DataPackage();
    }

    /**
     * 数据分封装
     *
     * @param bytes data
     * @return package
     */
    @Override
    public DataPackage toDataPackage(byte[] bytes) {
        if (bytes == null || bytes.length < RECEIVE_LENGTH) {
            Log.w(TAG, "数据解析失败:" + Arrays.toString(bytes));
            return null;
        }
        dataPackage.setOrigin(Arrays.copyOf(bytes, ORIGIN_LENGTH));
        dataPackage.setCmd(Arrays.copyOfRange(bytes,ORIGIN_LENGTH,ORIGIN_LENGTH+CMD_LENGTH));
        dataPackage.setData(Arrays.copyOfRange(bytes,ORIGIN_LENGTH+CMD_LENGTH,
                ORIGIN_LENGTH+CMD_LENGTH+DATA_LENGTH));

        return dataPackage;
    }

    /**
     * 数据拆包
     *
     * @param dataPackage package
     * @return data
     */
    @Override
    public byte[] fromDataPackage(DataPackage dataPackage) {
        byte[] bytes = new byte[SEND_LENGTH];
        System.arraycopy(dataPackage.getOrigin(),0,bytes,0,ORIGIN_LENGTH);
        System.arraycopy(dataPackage.getTarget(),0,bytes,ORIGIN_LENGTH, TARGET_LENGTH);
        System.arraycopy(dataPackage.getCmd(),0,bytes,ORIGIN_LENGTH+TARGET_LENGTH,CMD_LENGTH);
        System.arraycopy(dataPackage.getData(),0,bytes,ORIGIN_LENGTH+TARGET_LENGTH+CMD_LENGTH,DATA_LENGTH);
        return bytes;
    }
}
