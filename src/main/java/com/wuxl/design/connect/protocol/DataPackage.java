package com.wuxl.design.connect.protocol;

import java.util.Arrays;

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

    private byte[] cmd = new byte[0];

    //前3字节有效
    private byte[] data = new byte[0];

    public DataPackage() {}

    public DataPackage(byte[] origin, byte[] cmd, byte[] data) {
        this.origin = origin;
        this.cmd = cmd;
        this.data = data;
    }

    public DataPackage(byte[] origin, byte[] target, byte[] cmd, byte[] data) {
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

    public byte[] getCmd() {
        return cmd;
    }

    public void setCmd(byte[] cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getHexOrigin(){
        return toHex(origin);
    }

    public String getHexTarget(){
        return toHex(target);
    }

    @Override
    public String toString() {
        return "DataPackage{" +
                "origin=" + getHexOrigin() +
                ", target=" + getHexTarget() +
                ", cmd=" + Arrays.toString(cmd) +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
