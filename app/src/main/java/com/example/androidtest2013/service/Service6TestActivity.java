package com.example.androidtest2013.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class Service6TestActivity extends AppCompatActivity {

    private Messenger messenger = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 使用服务端的IBinder对象实例化一个Messenger对象
            messenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service6_test);

        findViewById(R.id.sv6_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService(new Intent(Service6TestActivity.this, Service6.class), serviceConnection, Service.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.sv6_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 实例化一个Message对象
                Message message = Message.obtain();
                message.what = Service6.MSG_SAY_HELLO;
                message.obj = 5;
                try {
                    // 把Message独享传递给服务端处理
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.sv6_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(serviceConnection);
                messenger = null;
            }
        });
    }
}
