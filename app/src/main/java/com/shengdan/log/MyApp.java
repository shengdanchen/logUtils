package com.shengdan.log;

import android.app.Application;

import com.shengdan.log_utils.LogThread;
import com.shengdan.log_utils.LogUtils;

/**
 * author : ChenShengDan
 * date   : 2023/1/16
 * desc   :
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init(this);
        LogThread.startLogThread(getExternalFilesDir("/myLogFile/").getPath());
    }
}
