package com.example.androidtest2013.service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class Service1TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service1_test);

        final Intent svIntent = new Intent(Service1TestActivity.this, Service1.class);
        findViewById(R.id.sv1_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(svIntent);
            }
        });

        findViewById(R.id.sv1_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(svIntent);
            }
        });
    }
}
