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
import android.widget.Toast;

import com.example.androidtest2013.R;

import java.util.List;
import java.util.Random;

public class Service8TestActivity extends AppCompatActivity {
    private IGetMsg getMsg;
    private static User[] users = new User[]{
        new User(0, "jack0", "99999999990"),
            new User(1, "jack1", "99999999991"),
            new User(2, "jack2", "99999999992"),
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            getMsg = IGetMsg.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            getMsg = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service8_test);

        findViewById(R.id.sv8_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.zdf.service8");
                //bindService(new Intent(Service8TestActivity.this, Service8.class), serviceConnection, Service.BIND_AUTO_CREATE);
                bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.sv8_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int nextInt = random.nextInt(2);
                try {
                    List<Message> msgs = getMsg.getMes(users[nextInt]);
                    StringBuffer stringBuffer = new StringBuffer();
                    for(Message msg : msgs){
                        stringBuffer.append(msg.toString() + "\n");
                    }
                    Toast.makeText(Service8TestActivity.this, stringBuffer.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("zdf", stringBuffer.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.sv8_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(serviceConnection);
                getMsg = null;
            }
        });
    }
}
