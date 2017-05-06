package com.wuxl.design.connect.protocol;

/**
 * 数据传输协议
 * 目的    来源   命令   数据       结束
 *  48      48     8     0~128位    8位
 *  6       6      1     16          1   byte
 * Created by wuxingle on 2017/4/9 0009.
 */
public class DataProtocol {

    public static final int TARGET_LENGTH = 6;

    public static final int ORIGIN_LENGTH = 6;

    public static final int DATA_LENGTH = 16;


    //数据包大小
    public static final int PACKET_MIN_LENGTH = 14;

    public static final int PACKET_MAX_LENGTH = 30;

    //结束位
    public static final byte DATA_END = 0x0a;

    //命令
    public static final byte OK = 0x11;

    public static final byte ONLINE = 0x12;

    public static final byte IS_APP = 0x21;

    public static final byte IS_MCU = 0x22;

    public static final byte FAIL = 0x33;

    public static final byte ADD_LED = 0x41;

    public static final byte UPING = 0x51;

    public static final byte DOWNING = 0x52;

    public static final byte ON = 0x61;

    public static final byte OFF = 0x62;

    public static final byte TIME_ON = 0x63;

    public static final byte TIME_OFF = 0x64;

    public static final byte TIME_CLR = 0x65;

}
