package com.shengdan.log;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shengdan.log_utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.info("This is tag","This is content");

    }
}