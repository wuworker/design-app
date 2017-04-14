package com.wuxl.design.connect.protocol;

import static com.wuxl.design.utils.DataUtils.toHex;

/**
 * 数据包
 * Created by wuxingle on 2017/4/9 0009.
 */
public class DataPackage {

    //数据来源
    private byte[] origin = new byte[0];

    //数据目的
    private byte[] target = new byte[0];

    private byte cmd;

    private int data;

    public DataPackage() {}

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

    public String getHexOrigin(){
        return toHex(origin);
    }

    public String getHexTarget(){
        return toHex(target);
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

    @Override
    public String toString() {
        return "DataPackage{" +
                "origin=" + toHex(origin) +
                ", target=" + toHex(target) +
                ", cmd=" + cmd +
                ", data=" + data +
                '}';
    }
}
