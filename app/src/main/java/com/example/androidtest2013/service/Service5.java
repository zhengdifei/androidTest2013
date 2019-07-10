package com.example.androidtest2013.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class Service5 extends Service {
    private final static String TAG = "Service5";
    private final int MULTIPLE = 1024;
    public final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // 绑定服务，把当前服务的IBinder对象的引用传递给宿主
        Log.e(TAG, "Service5 is binding.");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service5 is created!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service5 is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service5 is destroyed.");
    }

    public class LocalBinder extends Binder {
        // 在Binder中定义一个自定义的接口用于数据交互
        // 这里直接把当前的服务传回给宿主
        public Service5 getService(){
            return Service5.this;
        }
    }

    public int getMultipleNum(int num){
        // 定义一个方法 用于数据交互
        return MULTIPLE * num;
    }
}
