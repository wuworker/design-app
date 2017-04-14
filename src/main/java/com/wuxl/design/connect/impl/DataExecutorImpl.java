package com.wuxl.design.connect.impl;

import android.util.Log;

import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.protocol.DataPackage;

import java.util.Arrays;

import static com.wuxl.design.connect.protocol.DataProtocol.CMD_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.ORIGIN_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.RECEIVE_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.SEND_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.TARGET_LENGTH;
import static com.wuxl.design.utils.DataUtils.toByte;
import static com.wuxl.design.utils.DataUtils.toInteger;

/**
 * 数据解析封装的实现
 * Created by wuxingle on 2017/4/13 0013.
 */
public class DataExecutorImpl extends DataExecutor {

    private static final String TAG = "DataExecutorImpl";

    public DataExecutorImpl(TCPConnector tcpConnector) {
        super(tcpConnector);
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
        dataPackage.setCmd(bytes[ORIGIN_LENGTH]);
        dataPackage.setData(toInteger(bytes, ORIGIN_LENGTH + CMD_LENGTH));

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
        System.arraycopy(dataPackage.getOrigin(), 0, bytes, 0, ORIGIN_LENGTH);
        System.arraycopy(dataPackage.getTarget(), 0, bytes, ORIGIN_LENGTH, TARGET_LENGTH);
        bytes[ORIGIN_LENGTH + TARGET_LENGTH] = dataPackage.getCmd();
        toByte(bytes, dataPackage.getData(), ORIGIN_LENGTH + TARGET_LENGTH + CMD_LENGTH);
        return bytes;
    }
}
