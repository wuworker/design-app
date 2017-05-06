package com.wuxl.design.common.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by wuxingle on 2017/5/6 0006.
 * 日期工具类
 */
public class DateUtils {


    /**
     * 返回目标日期到现在的所有分钟数
     */
    public static int toNowAfterMinutes(int year, int month, int day, int hour, int minute) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month - 1, day, hour, minute);
        long differ = calendar.getTimeInMillis() - System.currentTimeMillis();
        return (int) ((differ / 1000 + 59) / 60);
    }

}



