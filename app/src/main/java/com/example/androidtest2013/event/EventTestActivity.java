package com.example.androidtest2013.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.androidtest2013.R;

public class EventTestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_test);

        findViewById(R.id.event1).setOnLongClickListener(new SendSmsListener(this));

        findViewById(R.id.event2).setOnClickListener(this);

        findViewById(R.id.event4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zdf", "匿名内部类");
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.e("zdf", "self on click");
    }

    public void btnClick(View view){
        Log.e("zdf", "event on click");
    }
}
