package com.wuxl.design.utils;

import org.junit.Test;

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
        int num = DataUtils.toInteger(bytes);
        System.out.println(num);
        System.out.println(Integer.toHexString(num));
    }

    @Test
    public void testToByte() throws Exception {

    }
}