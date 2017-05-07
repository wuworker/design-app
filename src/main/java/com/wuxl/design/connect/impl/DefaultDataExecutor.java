package com.wuxl.design.connect.impl;

import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataPackage;

import static com.wuxl.design.connect.protocol.DataProtocol.DATA_END;
import static com.wuxl.design.connect.protocol.DataProtocol.OFFEST;
import static com.wuxl.design.connect.protocol.DataProtocol.ORIGIN_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.PACKET_MIN_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.TARGET_LENGTH;

/**
 * Created by wuxingle on 2017/4/13 0013.
 * 默认的数据的解析实现
 */
public class DefaultDataExecutor extends DataExecutor {

    /**
     * 接收解析
     */
    @Override
    public DataPackage toDataPackage(byte[] bytes) {
        if (bytes == null || bytes.length < PACKET_MIN_LENGTH) {
            return null;
        }

        System.arraycopy(bytes, 0, dataPackage.getTarget(), 0, TARGET_LENGTH);
        System.arraycopy(bytes, TARGET_LENGTH, dataPackage.getOrigin(), 0, ORIGIN_LENGTH);
        dataPackage.setCmd(bytes[TARGET_LENGTH + ORIGIN_LENGTH]);
        int dataStart = TARGET_LENGTH + ORIGIN_LENGTH + 1;
        //去掉数据尾
        int dataLen = bytes.length - dataStart - 1;
        //去掉数据偏移
        byte[] packageData = dataPackage.getData();
        for(int i=0;i<dataLen;i++){
            packageData[i] = (byte)(bytes[dataStart + i] - OFFEST);
        }
        dataPackage.setDataLen(dataLen);
        return dataPackage;
    }

    /**
     * 发送解析
     */
    @Override
    public byte[] fromDataPackage(DataPackage dataPackage) {
        byte[] bytes = new byte[PACKET_MIN_LENGTH + dataPackage.getDataLen()];
        System.arraycopy(dataPackage.getTarget(), 0, bytes, 0, TARGET_LENGTH);
        System.arraycopy(dataPackage.getOrigin(), 0, bytes, TARGET_LENGTH, ORIGIN_LENGTH);
        bytes[TARGET_LENGTH + ORIGIN_LENGTH] = dataPackage.getCmd();
        //增加数据偏移
        byte[] packageData = dataPackage.getData();
        for(int i=0;i<dataPackage.getDataLen();i++){
            bytes[TARGET_LENGTH + ORIGIN_LENGTH + 1 + i] = (byte)(packageData[i] + OFFEST);
        }
        //增加数据尾
        bytes[bytes.length - 1] = DATA_END;
        return bytes;
    }

}
