package com.example.androidtest2013.camera;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidtest2013.R;

public class CameraSetActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_set);

        findViewById(R.id.ca_pc_btn).setOnClickListener(this);
        findViewById(R.id.ca_pv_btn).setOnClickListener(this);
        findViewById(R.id.ca_rs_btn).setOnClickListener(this);
        findViewById(R.id.ca_rv_btn).setOnClickListener(this);
        findViewById(R.id.ca_tp_btn).setOnClickListener(this);
        findViewById(R.id.ca_tv_btn).setOnClickListener(this);
        findViewById(R.id.ca_vv_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.ca_pc_btn:
                intent = new Intent(CameraSetActivity.this, PlayCameraTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ca_pv_btn:
                intent = new Intent(CameraSetActivity.this, PlayVedioTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ca_rs_btn:
                intent = new Intent(CameraSetActivity.this, MediaRecorderSoundActivity.class);
                startActivity(intent);
                break;
            case R.id.ca_rv_btn:
                intent = new Intent(CameraSetActivity.this, MediaRecorderVideoActivity.class);
                startActivity(intent);
                break;
            case R.id.ca_tp_btn:
                intent = new Intent(CameraSetActivity.this, TakePhototTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ca_tv_btn:
                intent = new Intent(CameraSetActivity.this, TakeVedioTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ca_vv_btn:
                intent = new Intent(CameraSetActivity.this, VideoViewTestActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
