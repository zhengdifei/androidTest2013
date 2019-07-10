package com.example.androidtest2013.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidtest2013.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClientActivity extends AppCompatActivity {
    public static Context context;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button btnConn, btnClose, btnClrrcv, btnClrsend,btnSend;
    private TextView tvRcv, tvSend;
    private EditText etSend, etIp, etPort;
    private SurfaceView svSend;
    private MyClicker myClicker = new MyClicker();
    private MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private ExecutorService exec = Executors.newCachedThreadPool();

    private static TcpClient tcpClient = null;

    private Camera camera;

    private long startTime, endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_client);

        context = this;
        bindId();
        bindListener();
        bindReceiver();
        init();
        initCamera();
    }

    private void bindId(){
        btnConn = findViewById(R.id.tcp_c_btn_conn);
        btnClose = findViewById(R.id.tcp_c_btn_close);
        btnClrrcv = findViewById(R.id.tcp_c_btn_clrrcv);
        btnClrsend = findViewById(R.id.tcp_c_btn_clrsend);
        btnSend = findViewById(R.id.tcp_c_btn_send);

        etIp = findViewById(R.id.tcp_c_et_ip);
        etPort = findViewById(R.id.tcp_c_et_port);
        etSend = findViewById(R.id.tcp_c_et_send);

        tvRcv = findViewById(R.id.tcp_c_tv_rcv);
        tvSend = findViewById(R.id.tcp_c_tv_send);

        svSend = findViewById(R.id.tcp_c_sv_send);
    }

    private void bindListener(){
        btnConn.setOnClickListener(myClicker);
        btnClose.setOnClickListener(myClicker);
        btnClrrcv.setOnClickListener(myClicker);
        btnClrsend.setOnClickListener(myClicker);
        btnSend.setOnClickListener(myClicker);
    }

    private void bindReceiver(){
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void init(){
        btnConn.setEnabled(true);
        btnClose.setEnabled(false);
        //btnSend.setEnabled(false);
    }

    private void initCamera(){
        SurfaceHolder surfaceHolder = svSend.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.e("zdf", "create camera");
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    Camera.Parameters parameters = camera.getParameters();
                    List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                    for(Camera.Size size: supportedPreviewSizes){
                        Log.e("zdf", "supported preview size:" + size.width + "X" + size.height);
                    }
                    parameters.setPreviewSize(Constants.width, Constants.height);
                    parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
                    parameters.setPreviewFpsRange(Constants.fpsMin, Constants.fpsMax);
                    camera.setParameters(parameters);
                    camera.setDisplayOrientation(90);
                    camera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] datas, Camera camera) {
                            if(camera == null) return;
                            if(tcpClient == null) return;

                            Log.e("zdf", "client 开始发送时间：" + sdf.format(new Date()));
                            startTime = System.currentTimeMillis();
                            //将原始camera数据转换成YuvImage对象，再转换成byte[]传输，否则在接收端需要进行先解析。
                            YuvImage image = new YuvImage(datas, ImageFormat.NV21, Constants.width, Constants.height, null);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 70, outputStream);
                            byte[] jpegData = outputStream.toByteArray();
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tcpClient.send(jpegData);
                            endTime = System.currentTimeMillis();
                            Log.e("zdf", "耗时：" + (endTime - startTime));
                        }
                    });
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }


    private void takePhoto(){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                tcpClient.send(bytes);
            }
        });
    }

    private void previewImage(){
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] datas, Camera camera) {
                if(camera == null) return;
                tcpClient.send(datas);
            }
        });
    }

    private class MyClicker implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tcp_c_btn_conn:
                    tcpClient = new TcpClient(etIp.getText().toString(), getPort(etPort.getText().toString()));
                    exec.execute(tcpClient);
                    break;
                case R.id.tcp_c_btn_close:
                    tcpClient.closeSelf();
                    break;
                case R.id.tcp_c_btn_clrrcv:
                    tvRcv.setText("");
                    break;
                case R.id.tcp_c_btn_clrsend:
                    tvSend.setText("");
                    takePhoto();
                    break;
                case R.id.tcp_c_btn_send:
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = etSend.getText().toString();
                    myHandler.sendMessage(message);
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            //1. 发送摄像头图片
                            //takePhoto();
                            //previewImage();
                            //initCamera();
                            //2. 发送固定的资源图片
                            /*Resources resources = getResources();
                            Drawable drawable = resources.getDrawable(R.drawable.image2);
                            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            byte[] imageBytes = outputStream.toByteArray();
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tcpClient.send(imageBytes);*/
                            //3. 发送固定文字
                            //tcpClient.send("hello zdf!");
                            //4. 发送EditView的文字
                            //tcpClient.send(etSend.getText().toString());
                        }
                    });
                    break;
            }
        }
    }

    private class MyHandler extends Handler{
        private WeakReference<TcpClientActivity> mActivity;

        public MyHandler(TcpClientActivity activity){
            mActivity = new WeakReference<TcpClientActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if(mActivity != null){
                switch (msg.what){
                    case 1:
                        tvRcv.append(msg.obj.toString());
                        break;
                    case 2:
                        tvSend.append(msg.obj.toString());
                        break;
                }
            }
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction){
                case "tcpClientReceiver":
                    String msg = intent.getStringExtra("tcpClientReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    private int getPort(String p){
        if(p.equals("")){
            return 9999;
        }
        return Integer.parseInt(p);
    }
}
