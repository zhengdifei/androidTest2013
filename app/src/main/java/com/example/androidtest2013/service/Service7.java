package com.example.androidtest2013.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Random;

public class Service7 extends Service {
    private final static String TAG = "Service7";
    private DogBinder binder = null;
    private String[] names=new String[]{"小白","旺财","小黑"};
    private int[] ages=new int[]{1,2,3};

    public class DogBinder extends IDog.Stub {
        @Override
        public String getName() throws RemoteException {
            Random random = new Random();
            int nextInt = random.nextInt(2);
            return names[nextInt];
        }

        @Override
        public int getAge() throws RemoteException {
            Random random = new Random();
            int nextInt = random.nextInt(2);
            return ages[nextInt];
        }

        @Override
        public void setAge(int age) throws RemoteException {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "Service7 return Binder");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service7 is created");
        // 实例化Binder对象
        binder = new DogBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service7 is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "Service7 is unbinded.");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service7 is destoryed.");
    }
}
