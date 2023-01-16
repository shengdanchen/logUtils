package com.shengdan.log_utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @data 2017/11/20
 */
public class DateUtils {
    private DateUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public enum Format {
        // SSS表示毫秒
        H_m_s_f("HH:mm:ss.SSS"), Y_M_d_H_m_s("yyyy-MM-dd HH:mm:ss"), Y_M_d("yyyy-MM-dd"), M_d_H_m_s("MM-dd HH:mm:ss"),
        M_d("MM-dd"), H_m_s("HH:mm:ss"), YMdHms("yyyyMMddHHmmss"),M_D_CHINESE("MM月dd日"), M_D_H_m_CHINESE("MM月dd日 HH:mm"),
        H_m("HH:mm"),Y_M_d_CHINESE("yyyy年MM月dd日");

        private final String formatStr;


        Format(String formatStr) {
            this.formatStr = formatStr;
        }


        public String getFormatStr() {
            return formatStr;
        }
    }

    public enum Unit {
        MS, Second
    }


    public static String formatDate(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date.getTime());
    }


    public static String formatDateTimeMs(String formatStr, long value) {
        return formatDateTime(formatStr, value, Unit.MS);
    }


    /**
     * 字符串转换成以秒为单位的long
     *
     * @throws ParseException
     */
    public static long stringFormatInt(String formatStr, String s) throws ParseException {
        DateFormat dateformat = new SimpleDateFormat(formatStr, Locale.getDefault());
        Date date = dateformat.parse(s);
        return TimeUnit.MILLISECONDS.toSeconds(date.getTime());
    }


    public static String formatDateTime(String formatStr, long value, Unit unit) {
        switch (unit) {
            case Second:
                value *= 1000;
                break;
            case MS:
            default:
                break;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.getDefault());

        String time = format.format(new Date(value));
        if (!TextUtils.isEmpty(time) && time.startsWith("0")) {
            time = time.substring(1);
        }
        return time;
    }


    public static long getDayStartTime(long ms) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(ms);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }


    public static long getDayEndTime(long ms) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(ms);
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        instance.set(Calendar.MILLISECOND, 999);
        return instance.getTimeInMillis();
    }


    /**
     * 计算时间差
     */
    public static final int CAL_MINUTES = 1000 * 60;
    public static final int CAL_HOURS = 1000 * 60 * 60;
    public static final int CAL_DAYS = 1000 * 60 * 60 * 24;


    /**
     * 获取当前时间格式化后的值
     */
    public static String getNowDateText(String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(new Date());
    }


    /**
     * 获取日期格式化后的值
     */
    public static String getDateText(Date date, String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(date);
    }


    /**
     * 字符串时间转换成Date格式
     *
     * @throws ParseException
     */
    public static Date getDate(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.parse(date);
    }


    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }


    /**
     * 获取时间戳
     */
    public static Long getTime(Date date) {
        return date.getTime();
    }


    /**
     * 计算时间差
     *
     * @param calType 计算类型,按分钟、小时、天数计算
     */
    public static int calDiffs(Date startDate, Date endDate, int calType) {
        Long start = DateUtils.getTime(startDate);
        Long end = DateUtils.getTime(endDate);
        int diff = (int) ((end - start) / calType);
        return diff;
    }


    /**
     * 计算时间差值以某种约定形式显示
     */
    public static String timeDiffText(Date startDate, Date endDate, Format format) {
        int calDiffs = DateUtils.calDiffs(startDate, endDate, DateUtils.CAL_MINUTES);
        if (calDiffs == 0) {
            return "刚刚";
        }
        if (calDiffs < 60) {
            return calDiffs + "分钟前";
        }
        //        calDiffs = DateUtils.calDiffs(startDate, endDate, DateUtils.CAL_HOURS);
        //        if(calDiffs < 24){
        //            return calDiffs + "小时前";
        //        }
        //        if(calDiffs < 48){
        //            return "昨天";
        //        }
        return DateUtils.getDateText(startDate, format.getFormatStr());
    }

    /**
     * 显示某种约定后的时间值,类似微信朋友圈发布说说显示的时间那种
     *
     * @param date
     * @return
     */
    //    public static String showTimeText(Date date){
    //        return DateUtils.timeDiffText(date, new Date());
    //    }


    /**
     * 显示某种约定后的时间值,类似微信朋友圈发布说说显示的时间那种
     */
    public static String showTimeText(long date, Format format) {
        return DateUtils.timeDiffText(new Date(date), new Date(), format);
    }


    /**
     *
     * @param mouth 从1开始计数
     * @param isSimple
     * @return
     */
    public static String getEngMouth(int mouth, boolean isSimple) {
        if (mouth > 12 || mouth < 1) {
            LogUtils.debug(null, "获取英文月份方法中 传递的月份数据有误!");
            return "";
        }

        if (isSimple) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, mouth - 1);
            DateFormat df = new SimpleDateFormat("MMM",Locale.ENGLISH);
            return StringUtils.upperFirstCase(df.format(calendar.getTime()));
        } else {
            String[] mouthStr = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            return mouthStr[mouth - 1];
        }
    }


    /**
     * 备注：根据传递进来的时间戳 变换为指定类型事件字符串：比如 2018-11-05
     */
    public static String getDateStr(String timetamp, String pattern) {
        try {
            if (!TextUtils.isEmpty(timetamp) && TextUtils.isDigitsOnly(timetamp)) {
                Date date = new Date(Long.parseLong(timetamp));
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                return dateFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串转时间戳
     * @param str
     * @return
     */
    public static long StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = format.parse(str);
                return date.getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }
}
