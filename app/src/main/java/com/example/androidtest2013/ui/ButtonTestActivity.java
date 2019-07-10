package com.example.androidtest2013.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.androidtest2013.R;

public class ButtonTestActivity extends AppCompatActivity {
    private int flag = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_test);

        final Button btn1 = findViewById(R.id.btn1);
        final Button btn2 = findViewById(R.id.btn2);
        final Button btn3 = findViewById(R.id.btn3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 1 && btn2.getWidth() == getWindowManager().getDefaultDisplay().getWidth()){
                    flag = -1;
                }else if(flag == -1 && btn2.getWidth() < 200){
                    flag = 1;
                }

                btn2.setWidth(btn2.getWidth() + (int)(btn2.getWidth() * 0.1)*flag);
                btn2.setHeight(btn2.getHeight() + (int)(btn2.getHeight() * 0.1)*flag);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btn2.setBackgroundResource(R.drawable.a1);
                }else {
                    btn2.setBackgroundResource(R.drawable.a2);
                }
                return false;
            }
        });
        //生成SpannableString，用于图片的载体
        SpannableString spannableString = new SpannableString("left");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a1);
        ImageSpan imageSpan = new ImageSpan(ButtonTestActivity.this, bitmap);
        spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        SpannableString spannableString2 = new SpannableString("right");
//        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.a2);
//        ImageSpan imageSpan2 = new ImageSpan(ButtonTestActivity.this, bitmap2);
//        spannableString2.setSpan(imageSpan2, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //把生成的SpannableString追加到按钮上
        btn3.setText(spannableString);
        //btn3.setText("zdf");
        //btn3.append(spannableString2);
    }
}
