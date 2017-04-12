package com.wuxl.design.connect.protocol;

import static com.wuxl.design.connect.protocol.DataProtocol.ORIGIN_LENGTH;
import static com.wuxl.design.connect.protocol.DataProtocol.TARGET_LENGTH;

/**
 * 数据包
 * Created by wuxingle on 2017/4/9 0009.
 */
public class DataPackage {

    //数据来源
    private byte[] origin = new byte[ORIGIN_LENGTH];

    //数据目的
    private byte[] target = new byte[TARGET_LENGTH];

    private byte cmd;

    private int data;

    public DataPackage() {}

    public DataPackage(byte[] origin, byte[] target, byte cmd, int data) {
        this.origin = origin;
        this.target = target;
        this.cmd = cmd;
        this.data = data;
    }

    public byte[] getOrigin() {
        return origin;
    }

    public void setOrigin(byte[] origin) {
        this.origin = origin;
    }

    public byte[] getTarget() {
        return target;
    }

    public void setTarget(byte[] target) {
        this.target = target;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
