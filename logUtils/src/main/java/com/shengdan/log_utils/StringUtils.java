package com.shengdan.log_utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import io.reactivex.annotations.NonNull;

/**
 */
public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();
    public final static String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f" };


    private StringUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static boolean isEmpty(CharSequence text) {
        if (TextUtils.isEmpty(text) || "null".equals(text)) {
            return true;
        }
        return text.toString().trim().length() <= 0;
    }


    public static String formatPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            return "";
        } else {
            Float toFloat = Float.parseFloat(price);
            return String.format("%.2f", toFloat);
        }
    }


    public static String upperFirstCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    /**
     * base64编码
     */
    public static String makeUidToBase64(String str) {
        String enUid = new String(Base64.encode(str.getBytes(), Base64.DEFAULT));
        LogUtils.debug(TAG, "base64编码: " + enUid);
        return enUid;
    }


    public static String appendUrlParam(String originVal, Map<String, String> parms) {
        StringBuilder stringBuilder = new StringBuilder(originVal);
        for (Map.Entry<String, String> entry : parms.entrySet()) {
            stringBuilder.append(stringBuilder.toString().contains("?") ? "&" : "?");
            stringBuilder.append(entry.getKey() + "=" + entry.getValue());
        }
        return stringBuilder.toString();
    }


    public static String ascii2Str(String asciicode) {
        String[] asciis = asciicode.split("\\\\u");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(asciis[0]);
        try {
            for (int i = 1; i < asciis.length; i++) {
                String code = asciis[i];
                stringBuilder.append((char) Integer.parseInt(code.substring(0, 4), 16));
                if (code.length() > 4) {
                    stringBuilder.append(code.substring(4, code.length()));
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return stringBuilder.toString().replace("\\", "");
    }


    /*
     * 判断一个字符串是否含有中文
     */
    public static boolean hasChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;// 有一个中文字符就返回
            }
        }
        return false;
    }


    /*
     * 判断一个字符是否是中文
     */
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }


    /**
     * base64解码
     */
    public static String analysisFromBase64(String base64Str) {
        String result = "";
        if (!TextUtils.isEmpty(base64Str)) {
            result = new String(Base64.decode(base64Str.getBytes(), Base64.DEFAULT));
        }
        return result;
    }


    private static Charset charset = Charset.forName("UTF-8");


    /**
     * 使用异或进行简单的密码加密
     */
    public static String setEncrypt(String secretKey, String str) {
        byte[] keyBytes = secretKey.getBytes(charset);

        byte[] b = str.getBytes(charset);
        for (int i = 0, size = b.length; i < size; i++) {
            for (byte keyBytes0 : keyBytes) {
                b[i] = (byte) (b[i] ^ keyBytes0);
            }
        }
        String result = new String(b);
        LogUtils.debug(TAG, "异或进行简单的密码加密: " + result);
        return result;
    }


    /**
     * 异或密码解密
     */
    public static String getEncrypt(String secretKey, String str) {
        byte[] keyBytes = secretKey.getBytes(charset);

        byte[] e = str.getBytes(charset);
        byte[] dee = e;
        for (int i = 0, size = e.length; i < size; i++) {
            for (byte keyBytes0 : keyBytes) {
                e[i] = (byte) (dee[i] ^ keyBytes0);
            }
        }
        String result = new String(e);

        LogUtils.debug(TAG, "异或密码解密: " + result);
        return result;
    }


    public static String getDataFromRaw(Context context,int rawId) {
        if (context == null)return "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().openRawResource(rawId));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static int chinese2Arabia(String chineseNumber) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[] { '一', '二', '三', '四', '五', '六', '七', '八', '九' };
        char[] chArr = new char[] { '十', '百', '千', '万', '亿' };
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if (0 != count) {//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if (b) {//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }

    //private static String[] unit_zh = new String[] { "", "十", "百", "千", "万", "亿" };


    public static String arabia2Chiness(Long number) {
        //number_zh unit_zh
        StringBuffer sb = new StringBuffer();

        String[] number_zh = new String[] { "零", "一", "二", "三", "四", "五", "六", "七", "八", "玖",
                "拾" };//拾是多出位
        String str = number.toString();
        Stack<String> _stack = new Stack<String>();
        for (int i = 0; i < str.length(); i++) {
            _stack.push(number_zh[(int) (number %
                                                 10)]);// 带 拾、佰、仟...  修改为  (int) (number % 10) + getUnitZH(Long.valueOf(i))
            number = number / 10;
        }
        while (!_stack.isEmpty()) {
            sb.append(_stack.pop());
        }
        return sb.toString();
    }


    public static String arabia2English(String num) {
        // 数字字符串参数
        // 判断字符串是否为数字
        if (!num.matches("\\d+")) {
            return String.format("%s is not number", num);
        }
        num = num.replaceAll("^[0]*([1-9]*)", "$1");

        String[] enNum = { // 基本数词表
                "zero", "one", "tow", "three", "four", "five", "six", "seven", "eight", "nine",
                "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
                "seventeen", "eighteen", "nineteen", "twenty", "", "", "", "", "", "", "", "", "",
                "thirty", "", "", "", "", "", "", "", "", "", "fourty", "", "", "", "", "", "", "",
                "", "", "fifty", "", "", "", "", "", "", "", "", "", "sixty", "", "", "", "", "",
                "", "", "", "", "seventy", "", "", "", "", "", "", "", "", "", "eighty", "", "", "",
                "", "", "", "", "", "", "ninety" };
        // 把字符串前面的0去掉
        if (num.length() == 0) {
            // 如果长度为0，则原串都是0
            return enNum[0];
        } else if (num.length() > 9) {
            // 如果大于9，即大于999999999，题目限制条件
            return "too big";
        }

        String[] enUnit = { "hundred", "thousand", "million", "billion", "trillion",
                "quintillion" }; // 单位表
        // 按3位分割分组
        int count = (num.length() % 3 == 0) ? num.length() / 3 : num.length() / 3 + 1;
        if (count > enUnit.length) {
            return "too big";
        } // 判断组单位是否超过，
        // 可以根据需求适当追加enUnit
        String[] group = new String[count];
        for (int i = num.length(), j = group.length - 1; i > 0; i -= 3) {
            group[j--] = num.substring(Math.max(i - 3, 0), i);
        }
        StringBuilder buf = new StringBuilder(); // 结果保存
        for (int i = 0; i < count; i++) { // 遍历分割的组
            int v = Integer.valueOf(group[i]);
            if (v >= 100) { // 因为按3位分割，所以这里不会有超过999的数
                buf.append(enNum[v / 100]).append(" ").append(enUnit[0]).append(" ");
                v = v % 100; // 获取百位，并得到百位以后的数
                if (v != 0) {
                    buf.append("and ");
                } // 如果百位后的数不为0，则追加and
            }
            if (v != 0) { // 前提是v不为0才作解析
                if (v < 20 || v % 10 == 0) {
                    // 如果小于20或10的整数倍，直接取基本数词表的单词
                    buf.append(enNum[v]).append(" ");
                } else { // 否则取10位数词，再取个位数词
                    buf.append(enNum[v - v % 10]).append(" ");
                    buf.append(enNum[v % 10]).append(" ");
                }
                if (i != count - 1) { // 百位以上的组追加相应的单位
                    buf.append(enUnit[count - 1 - i]).append(" ");
                }
            }
        }
        return buf.toString().trim(); // 返回值
    }


    /**
     * 将异常转换成字符串
     */
    public static String throwable2string(Throwable throwable) {
        String throwableString = null;
        try {
            StringWriter mStringWriter = new StringWriter();
            PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
            throwable.printStackTrace(mPrintWriter);
            mPrintWriter.close();
            throwableString = mStringWriter.toString();
        } catch (Exception e) {
        }
        return throwableString;
    }


    /**
     * 比较
     */
    public static boolean equals(String a, String b, boolean emptyEquals) {
        if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b)) {
            return emptyEquals;
        }
        return TextUtils.equals(a, b);
    }


    /**
     * 比较
     */
    public static boolean equalsIgnoreCase(String a, String b, boolean emptyEquals) {
        if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b)) {
            return emptyEquals;
        }
        if (!TextUtils.isEmpty(a)) {
            return a.equalsIgnoreCase(b);
        }
        return false;
    }


    /**
     * 是否包含
     */
    public static boolean containsIgnoreCase(String str, String key) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(key)) {
            return str.toLowerCase().contains(key.toLowerCase());
        }
        return false;
    }


    /**
     * 转化成小写
     */
    public static String toLowerCase(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.toLowerCase();
        }
        return text;
    }


    /**
     * 转化成大写
     */
    public static String toUpperCase(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.toUpperCase();
        }
        return text;
    }


    /**
     * List<String>转化成大写
     */
    @NonNull
    public static List<String> convert2Uppers(List<String> list) {
        List<String> strings = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (!TextUtils.isEmpty(s)) {
                    strings.add(s.toUpperCase());
                } else {
                    strings.add(s);
                }
            }
        }
        return strings;
    }


    /**
     * List<String>转化成小写
     */
    @NonNull
    public static List<String> convert2Lower(List<String> list) {
        List<String> strings = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (!TextUtils.isEmpty(s)) {
                    strings.add(s.toLowerCase());
                } else {
                    strings.add(s);
                }
            }
        }
        return strings;
    }


    /**
     * 查询集合中是否包括该字符串(大小写不区分)
     * 后期优化时间复杂度
     */
    public static boolean containsIgnoreCase(List<String> list, String data) {
        if (!TextUtils.isEmpty(data) && list != null) {
            return convert2Lower(list).contains(data.toLowerCase());
        }
        return false;
    }


    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     */
    public static String changeY2F(String amount) {
        if (!RegUtils.isAmount(amount)) {
            return null;
        }

        String currency = amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + "0");
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }


    /**
     * 将元为单位的转换为以元为单位XXX.00的格式
     */
    public static String formatAmount(String amount) {
        if (!RegUtils.isAmount(amount)) {
            return "";
        }

        String currency = amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();

        String amLong = currency;
        // 差值考虑小数点"."
        if (index == -1) {
            amLong = currency + ".00";
        } else if (length - index >= 3) {
        } else if (length - index == 2) {
            amLong = currency + "0";
        } else if (length - index == 1) {
            amLong = currency + "00";
        } else {
            //            amLong = currency + "00";
        }
        return amLong;
    }


    /**
     * 将分为单位的转换为元并返回金额格式的字符串 （除100）
     *
     * @throws Exception
     */
    public static String changeF2Y(String amount) throws Exception {
        if (!RegUtils.isAmount(amount)) {
            return null;
        }

        /**金额为分的格式 */
        String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

        if (!amount.matches(CURRENCY_FEN_REGEX)) {
            throw new Exception("金额格式有误");
        }

        int flag = 0;
        if (amount.charAt(0) == '-') {
            flag = 1;
            amount = amount.substring(1);
        }
        StringBuffer result = new StringBuffer();
        if (amount.length() == 1) {
            result.append("0.0").append(amount);
        } else if (amount.length() == 2) {
            result.append("0.").append(amount);
        } else {
            String intString = amount.substring(0, amount.length() - 2);
            for (int i = 1; i <= intString.length(); i++) {
                if ((i - 1) % 3 == 0 && i != 1) {
                    result.append(",");
                }
                result.append(intString
                        .substring(intString.length() - i, intString.length() - i + 1));
            }
            result.reverse().append(".").append(amount.substring(amount.length() - 2));
        }
        if (flag == 1) {
            return "-" + result.toString();
        } else {
            return result.toString();
        }
    }


    /**
     * 对字符串进行MD5编码
     */
    public static String md5Encode(String string) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }


    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }


    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n >>> 4 & 0xf;
        int d2 = n & 0xf;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }


    /**
     * 半角转换为全角
     */
    public String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {// 全角空格为12288，半角空格为32
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {// 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }


    /**
     * 格式化单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            //            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }


    /**
     * 备注： 获取指定url中的某个参数
     */
    public static String getParamByUrl(String url, String name) {
        String[] params = getUrlParams(url);
        if (params == null) {
            return "";
        }

        String value = "";
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue != null && keyValue.length > 1) {
                if (TextUtils.equals(keyValue[0], name)) {
                    value = keyValue[1];
                    break;
                }
            }
        }
        return value;
    }


    public static String[] getUrlParams(String url) {
        String[] urlParts = url.split("\\?");
        //没有参数
        if (urlParts.length == 1) {
            return null;
        }
        //有参数
        return urlParts[1].split("&");
    }


    /**
     * 备注人：yanghepeng
     * 备注时间：2019/3/13 下午5:25
     * 备注：去除请求url中的所有参数
     */
    public static String removeUrlParams(String originUrl) {
        try {
            if (originUrl.contains("?")) {
                originUrl = originUrl.substring(0, originUrl.indexOf("?"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originUrl;
    }


    public static String deleteSubString(String sourceStr, String deleteStr) {
        StringBuffer sb = new StringBuffer(sourceStr);
        int delCount = 0;

        String str = sourceStr;
        while (true) {
            int index = sb.indexOf(deleteStr);
            if (index == -1) {
                break;
            }
            sb.delete(index, index + deleteStr.length());
            delCount++;
        }
        if (delCount != 0) {
            str = sb.toString();
        }

        return str;
    }


    /**
     * 只能删除sourceString中唯一的char
     */
    public static String deleteCharString(String sourceStr, char deleteChar) {
        String tmpString = "";
        tmpString += deleteChar;
        StringBuffer stringBuffer = new StringBuffer(sourceStr);
        int iFlag = -1;
        do {
            iFlag = stringBuffer.indexOf(tmpString);
            if (iFlag != -1) {
                stringBuffer.deleteCharAt(iFlag);
            }
        } while (iFlag != -1);
        return stringBuffer.toString();
    }


    /**
     * 备注：unicode 转 String
     */
    public static String unicodeToString(String unicode) {
        StringBuffer sb = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int index = Integer.parseInt(hex[i], 16);
            sb.append((char) index);
        }
        return sb.toString();
    }


    /**
     * 备注：String 转 unicode
     */
    public static String stringToUnicode(String str) {
        StringBuffer sb = new StringBuffer();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            sb.append("\\u" + Integer.toHexString(c[i]));
        }
        return sb.toString();
    }


    /**
     * 备注：如果为空，取默认值
     */
    public static String isEmptyDef(String origin, String def) {
        if (!TextUtils.isEmpty(origin) && !"null".equals(origin)) {
            return origin;
        }
        return def;
    }
}
