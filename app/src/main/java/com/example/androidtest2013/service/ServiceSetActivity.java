package com.example.androidtest2013.service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class ServiceSetActivity extends AppCompatActivity {

    private MyClicker myClicker = new MyClicker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_set);

        findViewById(R.id.sv_btn1).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn2).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn3).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn4).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn5).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn6).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn7).setOnClickListener(myClicker);
        findViewById(R.id.sv_btn8).setOnClickListener(myClicker);

        startService(new Intent(ServiceSetActivity.this, IDog.Stub.class));
    }

    private class MyClicker implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case R.id.sv_btn1:
                    intent = new Intent(ServiceSetActivity.this, Service1TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn2:
                    intent = new Intent(ServiceSetActivity.this, Service2TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn3:
                    intent = new Intent(ServiceSetActivity.this, Service3TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn4:
                    intent = new Intent(ServiceSetActivity.this, Service4TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn5:
                    intent = new Intent(ServiceSetActivity.this, Service5TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn6:
                    intent = new Intent(ServiceSetActivity.this, Service6TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn7:
                    intent = new Intent(ServiceSetActivity.this, Service7TestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_btn8:
                    intent = new Intent(ServiceSetActivity.this, Service8TestActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }
}
