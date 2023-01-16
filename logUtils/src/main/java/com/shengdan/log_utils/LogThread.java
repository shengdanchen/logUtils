package com.shengdan.log_utils;

import static com.shengdan.log_utils.Utils.getNowTimeDay;
import static com.shengdan.log_utils.Utils.getTimeDay;
import static com.shengdan.log_utils.Utils.getYesterdayTimestamp;

import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : ChenShengDan
 * date   : 2022/2/12
 * desc   :
 */
public class LogThread extends Thread {

    //日志文件路径
//    public static String logFilePath = Environment.getExternalStorageDirectory() + "/clzxLog/";
    public static String logFilePath;

    //日志文件名
    public static String logFileName = "APP_" + getNowTimeDay() + ".log";
    private String oldLogFileName = "APP_" + getTimeDay(getYesterdayTimestamp()) + ".log";
    //日志文件大小上限 单位KB
    private int logFileLength = 20480; //kb
    //日志文件对象
    private File logFile;
    //控制日志是否记录
    public static boolean isRecord = true;
    FileOutputStream os = null;
    private String TAG = "RecordsLog";

    public LogThread(String logDir) {
        try {
            //
            logFilePath = logDir+"/log/";
            Log.d(TAG, "LogThread: "+logFilePath);

            File file = new File(logFilePath);
            if (!file.exists()) file.mkdir();

            File oldFile = new File(logFilePath + oldLogFileName);
            if (oldFile.exists())oldFile.delete();

            logFile = new File(logFilePath + logFileName);
            if (!logFile.exists()) logFile.createNewFile();

            os = new FileOutputStream(logFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (true) {
            int pid = android.os.Process.myPid();
            String[] running = new String[]{"logcat", "-s", "adb logcat *:V | findstr " + pid};
            Process exec = null;
            try {
                exec = Runtime.getRuntime().exec(running);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            final InputStream is = exec.getInputStream();
            try {
                int len = 0;
                byte[] buf = new byte[1024];
                Log.d(TAG, "run: Start reading log");
                while (-1 != (len = is.read(buf))) {
                    if (isRecord) {
                        if (!logFile.exists()){
                            os.close();
                            os = new FileOutputStream(logFile, true);
                        }

                        if (logFile.length() / 1024 > logFileLength) {
                            Log.d(TAG, "run: 日志大于" + logFileLength + "KB 清空");
                            clearInfoForFile(logFilePath + logFileName);
                        }
                        os.write(buf, 0, len);
                        os.flush();
                    }

                }
                Log.d(TAG, "run: end reading log");

            } catch (Exception e) {
                Log.d(TAG + "  writelog",
                        "read logcat process failed. message: "
                                + e.getMessage());
                return;
            }
        }


    }

    /**
     * 清空文件内容
     *
     * @param fileName
     */
    private static void clearInfoForFile(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启日志线程
     */
    public static void startLogThread(String logDir) {
        LogThread logThread = new LogThread(logDir);
        logThread.start();
    }

}
