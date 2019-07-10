package com.example.androidtest2013.camera;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.androidtest2013.R;

import java.io.File;
import java.io.IOException;

public class PlayVedioTestActivity extends AppCompatActivity {

    private static final String VEDIO_PATH = "/sdcard/SampleVideo_1280x720_5mb.mp4";
    private SurfaceView sv;
    private SeekBar sb1;
    private MediaPlayer mediaPlayer;
    private int position = 0;
    private boolean isPlaying;
    private Button play, replay, pause, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_vedio_test);

        play = findViewById(R.id.p_play);
        pause = findViewById(R.id.p_pause);
        replay = findViewById(R.id.p_replay);
        stop = findViewById(R.id.p_stop);

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

        sb1 = findViewById(R.id.p_sb1);
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当进度条停止修改的时候触发
                // 取得当前进度条的刻度
                int progress = sb1.getProgress();
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(progress);
                }
            }
        });

        sv = findViewById(R.id.p_sv1);
        sv.getHolder().addCallback(callback);
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.e("zdf", "surface create");
            if(position > 0){
                play(position);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.e("zdf", "holder 被改变");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.e("zdf", "销毁");
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                position = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }
    };

    private void play(final int msec){
        File file = new File(VEDIO_PATH);
        if(!file.exists()){
            Toast.makeText(this, "视频路径错误", Toast.LENGTH_SHORT).show();
            return;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setDisplay(sv.getHolder());
        Log.e("zdf", "开始装载");
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                Log.e("zdf", "装载完成");
                mediaPlayer.start();
                mediaPlayer.seekTo(msec);
                sb1.setMax(mediaPlayer.getDuration());
                new Thread(){
                    public void run(){
                        isPlaying = true;
                        while (isPlaying){
                            int current = mediaPlayer.getCurrentPosition();
                            sb1.setProgress(current);
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

                play.setEnabled(false);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play.setEnabled(true);
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                play(0);
                isPlaying = false;
                return false;
            }
        });
    }

    private void replay(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
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
            mediaPlayer.start();
            Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            pause.setText("继续");
            Toast.makeText(this, "暂停播放", Toast.LENGTH_SHORT).show();
        }
    }

    private void stop(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            play.setEnabled(true);
            isPlaying = false;
        }
    }
}
