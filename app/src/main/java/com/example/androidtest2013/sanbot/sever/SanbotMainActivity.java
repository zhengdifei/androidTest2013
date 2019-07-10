package com.example.androidtest2013.sanbot.sever;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidtest2013.R;
import com.example.androidtest2013.utils.SomeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SanbotMainActivity extends AppCompatActivity {

    private SurfaceView sv;
    private SurfaceHolder sh;
    private ImageView iv;
    private TextView tv;
    private String myHostIp = SomeUtils.getHostIp();
    private ListenerForBroadcast listenerForBroadcast = null;
    private ImageService imageService = null;
    private ControlService controlService = null;
    private MyHandler myHandler = new MyHandler();
    private Camera camera;
    private boolean isSendCameraPreview = false;
    private long startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanbot_main);

        bindComp();
        bindListener();
        startListenerBroadcast();
        startPreview();
        startImageService();
        startControlService();
    }

    private void bindComp(){
        sv = findViewById(R.id.sb_s_sv);
        iv = findViewById(R.id.sb_s_iv);
        tv = findViewById(R.id.sb_s_tvRcv);
    }

    private void bindListener(){

    }

    private void startImageService(){
        imageService = new ImageService(myHandler);
        new Thread(imageService).start();
    }

    private void startControlService(){
        controlService = new ControlService(myHandler);
        new Thread(controlService).start();
    }

    private void startListenerBroadcast(){
        if(!"".equals(myHostIp)){
            Log.e("zdf", "我的IP:" + myHostIp);
            listenerForBroadcast = new ListenerForBroadcast();
            new Thread(listenerForBroadcast).start();
        }else {
            Log.e("zdf", "获取不到本地host地址");
        }
    }

    /**
     * 开始预览
     */
    private void startPreview(){
        sh = sv.getHolder();
        sh.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                camera = Camera.open();
                initCamera(camera);
                camera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if(sh.getSurface() == null){
                    return;
                }
                camera.stopPreview();
                initCamera(camera);
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.e("zdf", "预览退出");
                camera.stopPreview();
            }
        });
    }

    /**
     * 初始化默认相机
     * @param cam
     */
    private void initCamera(Camera cam){
        if(cam != null){
            try {
                cam.setPreviewDisplay(sh);
                cam.setDisplayOrientation(90);
                Camera.Parameters params = cam.getParameters();
                params.setPreviewSize(Constants.width, Constants.height);
                params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
                params.setPreviewFpsRange(Constants.fpsMin, Constants.fpsMax);
                cam.setParameters(params);
                cam.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        if(isSendCameraPreview && imageService != null){
                            //Log.e("zdf", "发送一张图片");
                            startTime = System.currentTimeMillis();
                            YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, Constants.width, Constants.height, null);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 80, outputStream);
                            byte[] jpgData = outputStream.toByteArray();
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("zdf", "图片输出流关闭发生错误");
                            }

                            imageService.send(jpgData);
                            endTime = System.currentTimeMillis();
                            //Log.e("zdf", "发送一张图片耗时：" + (endTime - startTime));
                        }else {
                            //Log.e("zdf", "预览方法，不发送图片");
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.e("zdf", "Camera还未打开或者资源被占用。");
        }
    }

    /**
     * 根据手机的方向，设置预览的方向
     * @param activity
     * @return
     */
    private int getCameraOrienOrientation(Activity activity){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = 0;
        if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }else {
            result = (info.orientation -degrees + 360) % 360;
        }

        return result;
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.CLIENT_CONNECT_REQ:
                    isSendCameraPreview = true;
                    break;
                case Constants.CLIENT_DESTORY_REQ:
                    isSendCameraPreview = false;
                    if(imageService != null){
                        imageService.close();
                        startImageService();
                        Log.e("zdf", "重新启动图片服务");
                    }

                    if(controlService != null){
                        controlService.close();
                        startControlService();
                        Log.e("zdf", "重新启动控制服务");
                    }
                    break;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(camera != null){
            initCamera(camera);
            camera.startPreview();
        }
        Log.e("zdf", "服务恢复，机器人服务恢复预览");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        Log.e("zdf", "服务进入后台，停止预览功能");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerForBroadcast.close();
        imageService.close();
        controlService.close();
        listenerForBroadcast = null;
        imageService = null;
        controlService = null;
        Log.e("zdf", "机器人服务退出");
    }
}
