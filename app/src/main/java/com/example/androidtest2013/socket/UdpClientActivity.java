package com.example.androidtest2013.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpClientActivity extends AppCompatActivity {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button btnConn, btnClose, btnClear,btnSendClear, btnUdpSend;
    private TextView tvRcv, tvSend;
    private EditText etSend;
    private SurfaceView sv;

    public static Context context;
    private UDPClient udpClient;

    private Camera camera;
    private StringBuffer udpRcvStrBuf = new StringBuffer(), udpSendStrBuf = new StringBuffer();

    private MyHandler myHandler = new MyHandler(this);
    private MyBtnClick myBtnClick = new MyBtnClick();

    private long startTime, endTime;
    private ExecutorService exec = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_client);
        context = this;

        bindWidget();
        listening();
        bindReceiver();
        initWidget();
        //initCamera();
    }

    private void bindWidget(){
        btnConn = findViewById(R.id.udp_btn_conn);
        btnClose = findViewById(R.id.udp_btn_close);
        btnClear = findViewById(R.id.udp_btn_clear);
        btnUdpSend = findViewById(R.id.udp_btn_send);

        tvRcv = findViewById(R.id.udp_tv_recv);
        tvSend = findViewById(R.id.udp_tv_send);
        etSend = findViewById(R.id.udp_et_send);

        sv = findViewById(R.id.udp_sv_send);
    }

    private void listening(){
        btnConn.setOnClickListener(myBtnClick);
        btnClose.setOnClickListener(myBtnClick);
        btnClear.setOnClickListener(myBtnClick);
        btnUdpSend.setOnClickListener(myBtnClick);
    }

    private void bindReceiver(){
        IntentFilter udpRcvIntentFilter = new IntentFilter("udpRcvMsg");
        registerReceiver(broadcastReceiver, udpRcvIntentFilter);
    }
    private void initWidget(){
        btnUdpSend.setEnabled(false);
    }

    private void initCamera(){
        final SurfaceHolder sh = sv.getHolder();
        sh.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.e("zdf", "create camera");
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(sh);
                    camera.setDisplayOrientation(90);
                    Camera.Parameters params = camera.getParameters();
                    params.setPreviewSize(Constants.width, Constants.height);
                    params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
                    params.setPreviewFpsRange(Constants.fpsMin, Constants.fpsMax);
                    camera.setParameters(params);
                    camera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] datas, Camera camera) {
                            if(camera == null) return;
                            if(udpClient == null) return;

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
                            Log.e("zdf", "image len:" + jpegData.length);
                            udpClient.send(jpegData);
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

    private class MyHandler extends Handler{
        private final WeakReference<UdpClientActivity> uActivity;

        public MyHandler(UdpClientActivity activity) {
            uActivity = new WeakReference<UdpClientActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.e("zdf", "收到消息：" + msg.what);
            switch (msg.what){
                case 1:
                    udpRcvStrBuf.append(msg.obj.toString());
                    tvRcv.setText(udpRcvStrBuf.toString());
                    break;
                case 2:
                    udpSendStrBuf.append(msg.obj.toString());
                    tvSend.setText(udpSendStrBuf.toString());
                    break;
                case 3:
                    tvRcv.setText(udpRcvStrBuf.toString());
                    break;
            }
        }
    }

    class MyBtnClick implements Button.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.udp_btn_clear:
                    udpRcvStrBuf.delete(0, udpRcvStrBuf.length());
                    Message message = new Message();
                    message.what = 3;
                    myHandler.sendMessage(message);
                    break;
                case R.id.udp_btn_conn:
                    udpClient = new UDPClient();
                    exec.execute(udpClient);
                    btnConn.setEnabled(false);
                    btnClose.setEnabled(true);
                    btnUdpSend.setEnabled(true);
                    break;
                case R.id.udp_btn_close:
                    udpClient.setUdpLife(false);
                    btnConn.setEnabled(true);
                    btnClose.setEnabled(false);
                    btnUdpSend.setEnabled(false);
                    break;
                case R.id.udp_btn_send:
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 2;
                            if(!etSend.getText().toString().equals("")){
                                udpClient.send(etSend.getText().toString());
                                message.obj = etSend.getText().toString();
                                myHandler.sendMessage(message);
                            }
                        }
                    });
                    thread.start();
                    break;
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("zdf", "收到广播：udpRcvMsg");
            String mAction = intent.getAction();
            switch (mAction) {
                case "udpRcvMsg":
                    Message message = new Message();
                    message.obj = intent.getStringExtra("udpRcvMsg");
                    message.what = 1;
                    myHandler.sendMessage(message);
                    break;
            }
            /*if(intent.hasExtra("udpRcvMsg")){
                Message message = new Message();
                message.obj = intent.getStringExtra("udpRcvMsg");
                message.what = 1;
                myHandler.sendMessage(message);
            }*/
        }
    };
}
