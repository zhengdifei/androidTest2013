package com.example.androidtest2013.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.androidtest2013.utils.HttpUtils;

public class Service3 extends IntentService {
    private final static String TAG = "Service3";
    private final static String IMAGE_URL = "http://ww2.sinaimg.cn/bmiddle/9dc6852bjw1e8gk397jt9j20c8085dg6.jpg";

    public Service3(){
        super("Service3");
    }

    public Service3(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service3 is created!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service3 is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "HandleIntent is execute");
        HttpUtils.saveImageToDisk(IMAGE_URL, "/sdcard/" + System.currentTimeMillis() + ".jpg");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service3 is destroyed.");
    }
}
