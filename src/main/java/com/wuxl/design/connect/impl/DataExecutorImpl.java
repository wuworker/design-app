package com.wuxl.design.connect.impl;

import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.protocol.DataPackage;

/**
 * 数据解析封装的实现
 * Created by wuxingle on 2017/4/13 0013.
 */
public class DataExecutorImpl extends DataExecutor{

    public DataExecutorImpl(TCPConnector tcpConnector){
        super(tcpConnector);
    }

    /**
     * 数据分封装
     * @param bytes data
     * @return package
     */
    @Override
    public DataPackage toDataPackage(byte[] bytes) {

        return null;
    }

    /**
     * 数据拆包
     * @param dataPackage package
     * @return data
     */
    @Override
    public byte[] fromDataPackage(DataPackage dataPackage) {




        return new byte[0];
    }
}
