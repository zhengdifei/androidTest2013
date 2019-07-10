package com.example.androidtest2013.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class Service2 extends Service {
    private final static String TAG = "Service2";

    private int count;
    private boolean quit;
    private Thread thread;
    private MyBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        // 声明一个方法，把count暴露给外部程序。
        public int getCount(){
            return count;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "Service2 return Binder");
        return binder;
        //return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service2 is created");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!quit){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        });

        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service2 is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "Service2 is unbinded.");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service2 is destoryed.");
        this.quit = true;
    }
}
