package com.wuxl.design.connect.protocol;

/**
 * 数据传输协议
 * 来源    目的   命令   数据
 *  48      48     8     24
 *  6       6      1     3    byte
 * Created by wuxingle on 2017/4/9 0009.
 */
public class DataProtocol {

    public static final int ORIGIN_LENGTH = 6;

    public static final int TARGET_LENGTH = 6;

    public static final int CMD_LENGTH = 1;

    public static final int DATA_LENGTH = 3;

    public static final int SEND_LENGTH = ORIGIN_LENGTH + TARGET_LENGTH + CMD_LENGTH + DATA_LENGTH;

    public static final int RECEIVE_LENGTH = ORIGIN_LENGTH + CMD_LENGTH + DATA_LENGTH;

    //开启
    public static final byte CMD_ON = 0x12;

    //关闭
    public static final byte CMD_OFF = 0x49;

    //调整
    public static final byte CMD_PWM = (byte)0xa1;

}
