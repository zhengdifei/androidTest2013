package com.example.androidtest2013.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.androidtest2013.R;

import java.util.IdentityHashMap;

public class Service7TestActivity extends AppCompatActivity {
    private IDog iDog;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //第一种方法
            //iDog = (IDog) iBinder;
            //第二种方法
            iDog = IDog.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service7_test);

        findViewById(R.id.sv7_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.zdf.service7");
                bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.sv7_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.e("zdf", "name:" + iDog.getName() + ", age:" + iDog.getAge());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.sv7_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(serviceConnection);
                iDog = null;
            }
        });
    }
}
