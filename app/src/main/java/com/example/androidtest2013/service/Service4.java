package com.example.androidtest2013.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.androidtest2013.R;

public class Service4 extends Service {
    private final static String TAG = "Service4";

    private Notification notification;

    public Service4() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service4 is created!");
        // 声明一个通知，并对其进行属性设置
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Service4.this);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Forgound service start.");
        // 声明一个Intent，用于设置点击通知后开启的Activity
        Intent intent = new Intent(Service4.this, Service3TestActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(Service4.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();
        // 把当前服务设定为前台服务，并指定显示的通知。
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service4 is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service4 is destroyed.");
        stopForeground(false);
    }
}
