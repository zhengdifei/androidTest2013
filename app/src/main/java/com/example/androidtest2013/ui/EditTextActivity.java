package com.example.androidtest2013.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.androidtest2013.R;

import java.util.Random;

public class EditTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        final int[] DRAW_IMG_ID = {
                R.drawable.a1,
                R.drawable.a2,
                R.drawable.a3,
                R.drawable.a4,
                R.drawable.a1,
                R.drawable.a2,
                R.drawable.a3,
                R.drawable.a4
        };
        final EditText et1 = findViewById(R.id.et1);
        findViewById(R.id.et1_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int random = new Random().nextInt(8);
                Bitmap bit = BitmapFactory.decodeResource(getResources(), DRAW_IMG_ID[random]);
                ImageSpan imageSpan = new ImageSpan(EditTextActivity.this, bit);
                SpannableString spannableString = new SpannableString("img");
                spannableString.setSpan(imageSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                et1.append(spannableString);
            }
        });

        final EditText et2 = findViewById(R.id.et2);
        findViewById(R.id.et2_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = et2.getText().toString();
                if(!num.equals("123")){
                    et2.setError("请输入123");
                }
            }
        });
    }
}
