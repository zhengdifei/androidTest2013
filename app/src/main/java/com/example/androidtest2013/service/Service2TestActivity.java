package com.example.androidtest2013.service;

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

public class Service2TestActivity extends AppCompatActivity {

    private Service2.MyBinder binder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service2_test);

        final Intent intent = new Intent();
        // 指定开启服务的action
        intent.setAction("com.zdf.service2");

        findViewById(R.id.sv2_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 绑定服务到当前activity中
                bindService(intent, conn, BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.sv2_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder = null;
                unbindService(conn);
            }
        });

        findViewById(R.id.sv2_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binder != null){
                    Toast.makeText(Service2TestActivity.this, "Service2的Count值：" + binder.getCount(), Toast.LENGTH_SHORT).show();
                    Log.e("zdf", "Service的Count值：" + binder.getCount());
                }else {
                    Toast.makeText(Service2TestActivity.this, "Service2 未绑定。", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("zdf", "service connected");
            // 取得Service对象中的Binder对象
            binder = (Service2.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("zdf", "service disconnected");
        }
    };
}
