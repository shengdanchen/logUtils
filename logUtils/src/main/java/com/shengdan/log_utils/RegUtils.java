package com.shengdan.log_utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式校验工具类
 */
public class RegUtils {
    private RegUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 是否是数字
     */
    public static boolean isDigit(String strNum) {
        return !TextUtils.isEmpty(strNum) && strNum.trim().matches("[0-9]+");
    }


    public static boolean isX(String strNum) {
        return !TextUtils.isEmpty(strNum) && strNum.matches("[xX]");
    }


    /**
     * 是否是金额
     */
    public static boolean isAmount(String strNum) {
        if (TextUtils.isEmpty(strNum)) {
            return false;
        }

        // /^[0-9]+([.]{1}[0-9]{1,2})?$/
        return strNum.trim().matches("^(([0-9]{1}\\d*)|([0]{1}))([\\.]{1}(\\d){1,2})?$");
    }


    /**
     * 是否是手机号
     */
    public static boolean isPhone(String strNum) {
        if (!isDigit(strNum)) {
            return false;
        }

        //        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        String regExp = "^[1][3-9][0-9]{9}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(strNum.trim());
        return m.find();
    }


    /**
     * 是否是一定位数的数字
     */
    public static boolean isPlacesDigit(String strNum, int places) {
        return isDigit(strNum) && strNum.trim().length() == places;
    }


    /**
     * 是否是微信支付码
     */
    public static boolean isWeiXinPayCode(String strNum) {
        if (!isDigit(strNum)) {
            return false;
        }

        String regExp = "^(10|11|12|13|14|15)\\d{16}$";
        Pattern p = Pattern.compile(regExp);

        return p.matcher(strNum.trim()).find();
    }


    /**
     * 是否是IP表达式
     */
    public static boolean isIP(String strNum) {
        return !TextUtils.isEmpty(strNum) && strNum.trim()
                                                   .matches("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");
    }


    /**
     * 修改备注:
     * 我国当前的身份证号分为三种：
     *
     * 一、15位身份证号
     *
     * 二、18位身份证号（前17位位数字，最后一位为字母x）
     *
     * 三、18为身份证号（18位都是数字）
     * /**
     * 这是注释的：
     * 验证是否是18位数字+x的身份证
     */

    public static boolean isIdentityCardNum(String strNum) {
        if (TextUtils.isEmpty(strNum)) {
            return false;
        }
        String REGEX_ID_CARD = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        return Pattern.matches(REGEX_ID_CARD, strNum.trim());
        //String regString = "(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        //return strNum.trim().matches(regString/*"/^[1-9](\\d{16}|\\d{13})[0-9xX]$/"*/);
    }


    /**
     * 是否是uid
     * 纯数字、19位或者20位
     */
    public static boolean isUid(String strNum) {
        return isPlacesDigit(strNum, 19) || isPlacesDigit(strNum, 20);
    }


    /**
     * 是否是车主平台（绿蜘蛛）支付码
     * 8位的纯数字
     */
    public static boolean isLvPayCode(String payCode) {
        return isPlacesDigit(payCode, 8);
    }


    /**
     * 是否是合法的username
     * 不小于四位的字符
     */
    public static boolean isUserName(String username) {
        return username.matches("^.{4,}");
    }


    public static boolean isName(String name) {
        return name.matches("^[\\u4e00-\\u9fa5]+(·[\\u4e00-\\u9fa5]+)*$");
    }


    /**
     * 是否是合法的密码
     * 不小于六位的字符
     */
    public static boolean isPassword(String password) {
        LogUtils.debug("RegUtil", "" + password);
        boolean numberAndLetter = password
                .matches("\\S*[A-Za-z]\\S*[0-9]\\S*|\\S*[0-9]\\S*[A-Za-z]\\S*");
        boolean size6_16 = password.matches("^.{6,16}");
        return /*numberAndLetter && */size6_16;

        //        boolean hasNumber = password.matches("\\.*\\d+\\.*"); // 必须包含数字
        //        boolean hasLetter = password.matches("[a-zA-Z]+");//必须包含字母
        //        boolean size6_16 = password.matches("^.{6,16}");// 6-16位
        //        boolean noBlank = password.matches("\\S*");// 不能有空格
        //        LogUtils.debug("RegUtil","numberLetter:"+numberLetter+"hasNumber:"+hasNumber+"hasLetter:"+hasLetter+"size6_16:"+size6_16+"noBlank:"+noBlank);
        //        return hasNumber&&hasLetter&&size6_16&&noBlank;
        //        return true;
    }

    /**
     * 是否是合法的短信验证码
     * @param smsCode
     * @return
     */
    //    public static boolean isSmsCode(String smsCode){
    //        return isPlacesDigit(smsCode, 10);
    //    }


    /**
     * 正则替换参数
     */
    public static String replaceParamVal(String url, String name, String newVal) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(newVal)) {
            url = url.replaceAll("(" + name + "=[^&]*)", name + "=" + newVal);
        }
        return url;
    }


    /**
     * 备注：  校验邮箱
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(REGEX_EMAIL, email);
    }


    /**
     * 备注人：yanghepeng
     * 备注时间：2019/1/17
     * 备注：  校验url
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        String REGEX_URL = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
        return Pattern.matches(REGEX_URL, url);
    }
}
