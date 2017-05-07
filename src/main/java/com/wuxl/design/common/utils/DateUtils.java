package com.wuxl.design.common.utils;

/**
 * Created by wuxingle on 2017/5/6 0006.
 * 日期工具类
 */
public class DateUtils {

    /**
     * 返回距离现在的时间
     * day,hour,minute,second
     */
    public static int[] getCountdownTime(long time){
        int[] diffTime = new int[4];
        int diffSecond = (int)((time - System.currentTimeMillis())/1000);
        diffTime[3] = diffSecond % 60;
        diffTime[2] = diffSecond / 60 % 60;
        diffTime[1] = diffSecond / 3600 % 24;
        diffTime[0] = diffSecond / (3600 * 24);

        return diffTime;
    }

}



