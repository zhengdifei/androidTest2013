package com.example.androidtest2013.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.androidtest2013.R;

public class ProgressBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        final ProgressBar pb_visable = findViewById(R.id.pb_visible);
        final ProgressBar pb_hor = findViewById(R.id.pb_hor);

        findViewById(R.id.pb_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 如果是增加按钮，因为进度条的最大值限制在100，第一刻度限制在90.
                // 在此限度内，以1.2倍递增
                if(pb_hor.getProgress() < 90){
                    pb_hor.setProgress((int) (pb_hor.getProgress() * 1.2));
                }

                if(pb_hor.getSecondaryProgress() < 100){
                    pb_hor.setSecondaryProgress((int) (pb_hor.getSecondaryProgress() * 1.2));
                }
            }
        });

        findViewById(R.id.pb_reduce_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 如果是增加按钮，因为进度条的最大值限制在100，第一刻度限制在10.第二刻度限制在20
                // 在此限度内，以10点为基数进行递减。
                if(pb_hor.getProgress() > 10){
                    pb_hor.incrementProgressBy(-10);
                }
                if(pb_hor.getSecondaryProgress() > 20){
                    pb_hor.incrementSecondaryProgressBy(-10);
                }
            }
        });

        findViewById(R.id.pb_visible_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pb_visable.getVisibility() == View.VISIBLE){
                    pb_visable.setVisibility( View.GONE);
                }else {
                    pb_visable.setVisibility(View.VISIBLE);
                }
            }
        });

        SeekBar sb1 = findViewById(R.id.sb1);
        SeekBar sb2 = findViewById(R.id.sb2);

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("zdf", "seekbar1 当前位置：" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("zdf", "seekbar1 开始拖动");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("zdf", "seekbar1 停止拖动");
            }
        });

        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("zdf", "seekbar2 当前位置：" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("zdf", "seekbar2 开始拖动");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("zdf", "seekbar2 停止拖动");
            }
        });

        RatingBar rb1 = findViewById(R.id.rb1);
        RatingBar rb2 = findViewById(R.id.rb2);
        rb1.setMax(100);
        rb1.setProgress(20);
        rb1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.e("zdf", "ratingBar1 progress:" + ratingBar.getProgress() + "  rating:" + v);
            }
        });

        rb2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.e("zdf", "ratingBar2 progress:" + ratingBar.getProgress() + "  rating:" + v);
            }
        });
    }

}
