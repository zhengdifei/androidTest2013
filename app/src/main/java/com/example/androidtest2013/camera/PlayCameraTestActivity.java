package com.example.androidtest2013.camera;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.androidtest2013.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PlayCameraTestActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_camera_test);

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        FrameLayout frameLayout = findViewById(R.id.camera_pre);
        frameLayout.addView(cameraPreview);

        findViewById(R.id.camera_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] bytes, Camera camera) {
                                File picFile = new File("/sdcard/" + System.currentTimeMillis() + ".jpg");
                                try {
                                    FileOutputStream fos = new FileOutputStream(picFile);
                                    fos.write(bytes);
                                    fos.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
            }
        });
    }

    private Camera getCameraInstance(){
        Camera c = null;
        c = Camera.open();
        return c;
    }

    @Override
    protected void onDestroy(){
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onDestroy();
    }
}
