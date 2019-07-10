package com.example.androidtest2013.camera;

import android.content.Context;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidtest2013.R;

import java.io.File;
import java.io.IOException;

public class MediaRecorderVideoActivity extends AppCompatActivity {
    private Button btn1, btn2;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private SurfaceView surfaceView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder_video);

        context = this;
        btn1 = findViewById(R.id.mrv_btn1);
        btn2 = findViewById(R.id.mrv_btn2);
        btn2.setEnabled(false);
        surfaceView = findViewById(R.id.mrv_sv);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });
    }

    protected void start(){
        File file = new File("/sdcard/video.mp4");
        if(file.exists()){
            file.delete();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();

        // 设置音频录入源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置视频图像的录入源
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录入媒体的输出格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置视频的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        // 设置视频的采样率，每秒4帧
        mediaRecorder.setVideoFrameRate(4);
        // 设置录制视频文件的输出路径
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        // 设置捕获视频图像的预览界面
        mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                btn1.setEnabled(true);
                btn2.setEnabled(false);
                Toast.makeText(context, "录像出错", Toast.LENGTH_SHORT).show();
            }
        });

        //准备，开始
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            btn1.setEnabled(false);
            btn2.setEnabled(true);
            isRecording = true;
            Toast.makeText(context, "录像开始", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void stop(){
        if(isRecording){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            btn1.setEnabled(true);
            btn2.setEnabled(false);
            Toast.makeText(context, "录像结束", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy(){
        if(isRecording){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }
}
