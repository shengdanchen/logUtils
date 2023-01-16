package com.shengdan.log_utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : ChenShengDan
 * date   : 2023/1/16
 * desc   :
 */
public class Utils {
    public static String getNowTimeDay() {
        long time = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        if (time == 0) {
            time = Calendar.getInstance().getTimeInMillis();
        }
        return sdf.format(time);
    }

    public static String getTimeDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        if (time == 0) {
            time = Calendar.getInstance().getTimeInMillis();
        }
        return sdf.format(time);
    }

    /**
     * 获取昨天的时间戳
     * @return
     */
    public static long getYesterdayTimestamp() {
        Calendar calendar = Calendar.getInstance();     //当前时间
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        return calendar.getTime().getTime();
    }
}
