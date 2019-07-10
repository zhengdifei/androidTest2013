package com.example.androidtest2013.video;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.androidtest2013.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainVideoActivity extends AppCompatActivity {
    private MyHandler myHandler;
    private ClientThread clientThread;
    private ByteArrayOutputStream outputStream;
    private Button btnStart, btnClose;
    private SurfaceView sv;
    private SurfaceHolder sh;
    private Camera camera;
    private boolean isPreview = false;
    private int screenWidth, screenHeight = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_video);

        myHandler = new MyHandler();
        clientThread = new ClientThread(myHandler);
        new Thread(clientThread).start();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.widthPixels;
        screenWidth = dm.heightPixels;

        btnStart = findViewById(R.id.vd_btn_start);
        btnClose = findViewById(R.id.vd_btn_stop);
        sv = findViewById(R.id.vd_sv);
        sh = sv.getHolder();
        sh.setFixedSize(screenWidth, screenHeight/4*3);

        sh.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(camera != null){
                    if(isPreview){
                        camera.stopPreview();
                    }
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    private void initCamera(){
        if(!isPreview){
            camera = Camera.open();
            ClientThread.size = camera.getParameters().getPreviewSize();
        }

        if(camera != null && !isPreview){
            try {
                // 通过SurfaceView显示取景画面
                camera.setPreviewDisplay(sh);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(screenWidth, screenHeight/4*3);
                /* 每秒从摄像头捕获5帧画面， */
                parameters.setPreviewFrameRate(5);
                parameters.setPictureFormat(ImageFormat.NV21);
                parameters.setPictureSize(screenWidth, screenHeight/4*3);
                camera.setDisplayOrientation(90);
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] datas, Camera camera) {
                        Camera.Size size = camera.getParameters().getPreviewSize();
                        YuvImage image = new YuvImage(datas, ImageFormat.NV21, size.width, size.height, null);
                        if(image != null){
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 70, outputStream);
                            byte[] jpegData = outputStream.toByteArray();
                            /**
                             * 温馨提示： 此处如果直接将YueImage传递出去，在子线程中进行YuvImage的压缩操作，将出出现内存异常的情况
                             * https://blog.csdn.net/q979713444/article/details/80446404
                             * 系统级的bug，建议使用第三方的图片压缩方法。
                             * 但是，在此处进行压缩操作，变成byte[]传递出去，则无内存不足异常出现。
                             * 本案例解决stream结束符的问题，是通过每个thread建立一个socket连接，传递一张图片，则销毁重建。
                             * 感觉耗费一些资源，但是图片传输的性能很好，视频延迟的情况也好很多。
                             */
                            Message msg = clientThread.revHandler.obtainMessage();
                            msg.what = 0x111;
                            msg.obj = jpegData;
                            clientThread.revHandler.sendMessage(msg);
                        }
                    }
                });
                // 开始预览 ,自动对焦
                camera.startPreview();
                camera.autoFocus(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    public static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x222){
                //返回信息显示代码
            }
        }
    }
}
