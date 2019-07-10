package com.example.androidtest2013.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.example.androidtest2013.R;

public class ToggleButtonTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle_button_test);

        ToggleButton tb = findViewById(R.id.tog_btn);
        final LinearLayout ll = findViewById(R.id.ori_layout);

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if( b ){
                    ll.setOrientation(LinearLayout.VERTICAL);
                }else {
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                }
            }
        });
    }
}