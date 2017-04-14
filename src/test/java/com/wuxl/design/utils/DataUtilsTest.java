package com.wuxl.design.utils;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by wuxingle on 2017/4/13 0013.
 *
 */
public class DataUtilsTest {

    @Test
    public void testToInteger() throws Exception {
        byte[] bytes = {
                0x10,(byte)0xfa,(byte)0xa9,0x11,0x09
        };
        System.out.println(Arrays.toString(bytes));
        int num = DataUtils.toInteger(bytes);
        System.out.println(Integer.toHexString(num));
        byte[] newbytes = DataUtils.toByte(num);
        System.out.println(Arrays.toString(newbytes));
    }

    @Test
    public void testToByte() throws Exception {

    }
}