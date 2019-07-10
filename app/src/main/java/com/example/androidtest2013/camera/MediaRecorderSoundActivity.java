package com.example.androidtest2013.camera;

import android.content.Context;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidtest2013.R;

import java.io.File;
import java.io.IOException;

public class MediaRecorderSoundActivity extends AppCompatActivity {
    private Button btn1, btn2;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder_sound);
        context = this;

        btn1 = findViewById(R.id.mr_btn1);
        btn2 = findViewById(R.id.mr_btn2);
        btn2.setEnabled(false);
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
        File file = new File("/sdcard/mr.amr");
        if(file.exists()){
            file.delete();
        }

        mediaRecorder = new MediaRecorder();
        // 设置音频录入源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置录制音频的输出格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置录制音频文件输出文件路径
        mediaRecorder.setOutputFile(file.getAbsolutePath());

        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                btn1.setEnabled(true);
                btn2.setEnabled(false);
                Toast.makeText(context, "录音出错", Toast.LENGTH_SHORT).show();
            }
        });

        //准备，开始
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            btn1.setEnabled(false);
            btn2.setEnabled(true);
            Toast.makeText(context, "开始录音", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "录音结束", Toast.LENGTH_SHORT).show();
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
