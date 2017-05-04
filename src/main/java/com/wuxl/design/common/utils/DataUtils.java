package com.wuxl.design.common.utils;

/**
 * 公共的工具类
 * Created by wuxingle on 2017/4/9 0009.
 */
public class DataUtils {

    public static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 转为16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String toHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HEX_CHARS[(b & 0xff) >> 4]);
            sb.append(HEX_CHARS[(b & 0x0f)]);
        }
        return sb.toString();
    }

    /**
     * 根据byte拿到int
     * byte[3] byte[2] byte[1] byte[0]
     * hh8    hl8     lh8     ll8
     */
    public static int toInteger(byte[] bytes) {
        return toInteger(bytes, 0);
    }

    public static int toInteger(byte[] bytes, int start) {
        if (bytes == null) {
            return 0;
        }
        int data = 0;
        for (int i = 0; start + i < bytes.length && i < 4; i++) {
            data |= ((bytes[start + i] & 0xff) << (i * 8));
        }
        return data;
    }

    /**
     * 转为byte
     * byte[3] byte[2] byte[1] byte[0]
     * hh8    hl8     lh8     ll8
     */
    public static byte[] toByte(int num) {
        byte[] bytes = new byte[4];
        toByte(bytes, num, 0);
        return bytes;
    }

    public static void toByte(byte[] bytes, int num, int start) {
        if (bytes == null) {
            return;
        }
        for (int i = 0; i + start < bytes.length && i < 4; i++) {
            bytes[start + i] = (byte) ((num >> (i * 8)) & 0xff);
        }

    }

    /**
     * 16进制转byte
     */
    public static byte[] toByte(String hex){
        if (hex == null) {
            return new byte[0];
        }
        byte[] bytes = new byte[(hex.length() + 1) / 2];
        String sHex = hex.toLowerCase();
        try {
            for (int i = 0; i < hex.length(); i += 2) {
                int h = indexOfHex(sHex.charAt(i));
                int l = (i + 1) < hex.length() ? indexOfHex(sHex.charAt(i + 1)) : 0;
                bytes[(i + 1) / 2] = (byte) ((h & 0x0f) << 4 | (l & 0x0f));
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
            return new byte[0];
        }
        return bytes;
    }

    /**
     * 单个16进制转byte
     */
    public static int indexOfHex(char c)throws NumberFormatException{
        int position = "0123456789abcdef".indexOf(c);
        if(position==-1){
            throw new NumberFormatException("输入的不是16进制数");
        }
        return position;
    }

}
