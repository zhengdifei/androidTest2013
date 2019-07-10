package com.example.androidtest2013.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.androidtest2013.R;

import java.util.ArrayList;
import java.util.List;

public class RadioTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_test);
        final RadioGroup rg = findViewById(R.id.gender);
        Button rb = findViewById(R.id.radio_btn);

        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int len = rg.getChildCount();
                String msgString = "";
                for(int i =0;i< len;i++){
                    RadioButton radioButton = (RadioButton) rg.getChildAt(i);
                    if(radioButton.isChecked()){
                        msgString = "you choose to be a " + radioButton.getText().toString();
                        break;
                    }
                }

                if(msgString.equals("")){
                    Log.e("zdf", "choose empty");
                }else {
                    Log.e("zdf", msgString);
                }
            }
        });
    }
}
