package com.example.androidtest2013.ui;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.example.androidtest2013.R;

import java.util.ArrayList;
import java.util.List;

public class ImageSwitcherActivity extends AppCompatActivity {

    private List<Drawable> list;
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_switcher);
        putData();
        final ImageSwitcher is1 = findViewById(R.id.is1);
        findViewById(R.id.is_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index--;
                if(index < 0){
                    index = list.size() -1;
                }
                is1.setImageDrawable(list.get(index));
            }
        });

        findViewById(R.id.is_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if(index >= list.size()){
                    index = 0;
                }

                is1.setImageDrawable(list.get(index));
            }
        });
        //通过代码设定从左缓进，从右换出的效果。
        is1.setInAnimation(AnimationUtils.loadAnimation(ImageSwitcherActivity.this, android.R.anim.slide_in_left));
        is1.setOutAnimation(AnimationUtils.loadAnimation(ImageSwitcherActivity.this, android.R.anim.slide_out_right));

        is1.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(ImageSwitcherActivity.this);
            }
        });

        is1.setImageDrawable(list.get(0));
    }

    private void putData(){
        list = new ArrayList<Drawable>();
        list.add(getResources().getDrawable(R.drawable.image1));
        list.add(getResources().getDrawable(R.drawable.image2));
        list.add(getResources().getDrawable(R.drawable.image3));
    }
}
