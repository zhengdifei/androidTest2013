package com.example.androidtest2013.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Service1 extends Service {
    private final static String TAG = "Service1";

    @Override
    public IBinder onBind(Intent intent) {
        // 仅通过startService()启动服务，所以这个方法返回null即可。
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service is created!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service is destroyed.");
    }
}
