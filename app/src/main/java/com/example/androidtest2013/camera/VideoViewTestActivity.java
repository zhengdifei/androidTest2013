package com.example.androidtest2013.camera;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.androidtest2013.R;
import com.example.androidtest2013.ui.ButtonTestActivity;

import java.io.File;
import java.io.IOException;

public class VideoViewTestActivity extends AppCompatActivity {
    private static final String VEDIO_PATH = "/sdcard/SampleVideo_1280x720_5mb.mp4";
    private Button play, pause, replay, stop;
    private VideoView vv;
    private SeekBar sb;
    private boolean isPlaying;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view_test);

        play = findViewById(R.id.vv_play);
        pause = findViewById(R.id.vv_pause);
        replay = findViewById(R.id.vv_replay);
        stop = findViewById(R.id.vv_stop);
        sb = findViewById(R.id.vv_sb1);
        vv = findViewById(R.id.vv1);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(0);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replay();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(vv != null && vv.isPlaying()){
                    vv.seekTo(progress);
                }
            }
        });
        final Context context = this;
        mediaController = new MediaController(this);
        File file = new File(VEDIO_PATH);
        if(file.exists()){
            vv.setVideoPath(file.getAbsolutePath());
            vv.setMediaController(mediaController);
            mediaController.setMediaPlayer(vv);
            mediaController.setPrevNextListeners(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "下一个", Toast.LENGTH_SHORT).show();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "上一个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void play(final int msec){
        File file = new File(VEDIO_PATH);
        if(!file.exists()){
            Toast.makeText(this, "视频路径错误", Toast.LENGTH_SHORT).show();
            return;
        }

        vv.setVideoPath(file.getAbsolutePath());
        Log.e("zdf", "开始播放");
        vv.start();
        vv.seekTo(msec);
        sb.setMax(vv.getDuration());
        new Thread(){
            public void run(){
                isPlaying = true;
                while (isPlaying){
                    int current = vv.getCurrentPosition();
                    sb.setProgress(current);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        play.setEnabled(false);

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play.setEnabled(true);
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                play(0);
                isPlaying = false;
                return false;
            }
        });
    }

    private void replay(){
        if(vv != null && vv.isPlaying()){
            vv.seekTo(0);
            Toast.makeText(this, "重新播放", Toast.LENGTH_SHORT).show();
            pause.setText("暂停");
            return;
        }

        isPlaying = false;
        play(0);
    }

    private void pause(){
        if(pause.getText().equals("继续")){
            pause.setText("暂停");
            vv.start();
            Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
            return;
        }

        if(vv != null && vv.isPlaying()){
            vv.pause();
            pause.setText("继续");
            Toast.makeText(this, "暂停播放", Toast.LENGTH_SHORT).show();
        }
    }

    private void stop(){
        if(vv != null && vv.isPlaying()){
            vv.stopPlayback();
            play.setEnabled(true);
            isPlaying = false;
        }
    }
}
