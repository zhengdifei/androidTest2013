package com.example.androidtest2013.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.androidtest2013.R;

public class Service5TestActivity extends AppCompatActivity {
    private Service5 service5;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Service5.LocalBinder localBinder = (Service5.LocalBinder) iBinder;
            service5 = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service5 = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service5_test);

        final Intent intent = new Intent(Service5TestActivity.this, Service5.class);

        findViewById(R.id.sv5_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zdf", "start bind");
                bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.sv5_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(Service5TestActivity.this, "result:" + service5.getMultipleNum(5), Toast.LENGTH_SHORT).show();
                Log.e("zdf", "result:" + service5.getMultipleNum(5));
            }
        });

        findViewById(R.id.sv5_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stopService(intent);
                unbindService(serviceConnection);
            }
        });
    }


}
