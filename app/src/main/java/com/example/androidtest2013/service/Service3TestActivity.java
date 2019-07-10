package com.example.androidtest2013.service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class Service3TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service3_test);
        final Intent intent = new Intent(Service3TestActivity.this, Service3.class);

        findViewById(R.id.sv3_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(intent);
            }
        });

        findViewById(R.id.sv3_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent);
            }
        });
    }
}
