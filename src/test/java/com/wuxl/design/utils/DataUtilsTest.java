package com.wuxl.design.utils;

import com.wuxl.design.common.utils.DataUtils;

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
    public void testToByte(){
        byte[] bytes1 = DataUtils.toByte("ab12345A");
        System.out.println(Arrays.toString(bytes1));
        String hex = DataUtils.toHex(bytes1);
        System.out.println(hex);
    }
}