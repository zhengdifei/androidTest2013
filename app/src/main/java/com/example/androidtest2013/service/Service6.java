package com.example.androidtest2013.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class Service6 extends Service {
    private final static String TAG = "Service6";
    public final static int MSG_SAY_HELLO = 1;
    private int num = 1024;
    private IncomingHandler incomingHandler = new IncomingHandler();
    private final Messenger messager = new Messenger(incomingHandler);

    public class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "Service say hello:" + (Integer)msg.obj * num, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Service say hello:" + (Integer)msg.obj * num);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messager.getBinder();
    }
}
