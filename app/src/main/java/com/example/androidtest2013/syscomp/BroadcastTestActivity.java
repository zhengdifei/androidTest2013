package com.example.androidtest2013.syscomp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class BroadcastTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_test);

        findViewById(R.id.br_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.zdf.broadcast1");
                intent.putExtra("msg", "这是个普通消息");
                sendBroadcast(intent);
            }
        });

        findViewById(R.id.br_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.zdf.broadcast1");
                intent.putExtra("msg", "这是个有序消息");
                sendOrderedBroadcast(intent, null);
            }
        });
    }
}
