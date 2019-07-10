package com.example.androidtest2013.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidtest2013.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class UdpServerActivity extends AppCompatActivity {
    private Button btnStart, btnClose, btnSendClear, btnRcvClear,btnSend;
    private TextView tvRcv, tvSend;
    private EditText etSend, etIp, etPort;
    private ImageView ivRcv;
    public static Context context;
    public static byte[] imgBuffer = null;

    private MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private MyBtnClick myBtnClick = new MyBtnClick();
    private static UDPServer udpServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_server);

        context = this;
        bindWidget();
        listening();
        bindReceiver();
        init();
    }

    private void bindWidget(){
        btnStart = findViewById(R.id.udp_s_btn_start);
        btnClose= findViewById(R.id.udp_s_btn_close);
        btnRcvClear= findViewById(R.id.udp_s_btn_clearRcv);
        btnSendClear = findViewById(R.id.udp_s_btn_clearSend);
        btnSend = findViewById(R.id.udp_s_btn_send);

        etIp = findViewById(R.id.udp_s_et_ip);
        etPort = findViewById(R.id.udp_s_et_port);
        etSend = findViewById(R.id.udp_s_et_send);

        tvRcv = findViewById(R.id.udp_s_tv_rcv);
        tvSend = findViewById(R.id.udp_s_tv_send);

        ivRcv = findViewById(R.id.udp_iv_rcv);
    }

    private void listening(){
        btnStart.setOnClickListener(myBtnClick);
        btnClose.setOnClickListener(myBtnClick);
        btnRcvClear.setOnClickListener(myBtnClick);
        btnSendClear.setOnClickListener(myBtnClick);
        btnSend.setOnClickListener(myBtnClick);
    }

    private void bindReceiver(){
        IntentFilter intentFilter = new IntentFilter("udpReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void init(){
        etIp.setText(getHostIp());
        etPort.setText(getPort("") + "");
        btnStart.setEnabled(true);
        btnClose.setEnabled(false);
    }

    private class MyHandler extends Handler{
        private final WeakReference<UdpServerActivity> mActivity;
        public MyHandler(UdpServerActivity activity){
            mActivity = new WeakReference<UdpServerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            Log.e("zdf", "收到消息：" + msg.what);
            UdpServerActivity activity = mActivity.get();
            if(null != activity){
                switch (msg.what){
                    case 1:
                        String str = msg.obj.toString();
                        tvRcv.append(str);
                        break;
                    case 2:
                        String stra = msg.obj.toString();
                        tvSend.append(stra);
                        break;
                    case 3:
                        Log.e("zdf", "image len:" + imgBuffer.length);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBuffer, 0, imgBuffer.length);
                        ivRcv.setImageBitmap(bitmap);
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
                case "udpReceiver":
                    Log.e("zdf", "收到广播：udpReceiver");
                    //1. 收到文字信息
                    String msg = intent.getStringExtra("udpReceiver");
                    Message message = new Message();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);

                    //2. 收到图片信息
                    /*Message message = new Message();
                    message.what = 3;
                    myHandler.sendMessage(message);*/
                    break;
            }
        }
    }

    private class MyBtnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.udp_s_btn_start:
                    if(!etIp.getText().toString().isEmpty() && ! etPort.getText().toString().isEmpty()){
                        int mPort = Integer.parseInt(etPort.getText().toString());
                        udpServer = new UDPServer(etIp.getText().toString(), mPort);
                        Thread thread = new Thread(udpServer);
                        thread.start();
                        btnStart.setEnabled(false);
                        btnClose.setEnabled(true);
                    }else {
                        Log.e("zdf", "ip, port");
                    }
                    break;
                case R.id.udp_s_btn_close:
                    btnClose.setEnabled(false);
                    btnStart.setEnabled(true);
                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            udpServer.setUdpLife(false);
                            while (udpServer.getLifeMsg());

                            Looper.getMainLooper();

                        }
                    });
                    thread.start();
                    break;
                case R.id.udp_s_btn_clearRcv:
                    tvRcv.setText("");
                    break;
                case R.id.udp_s_btn_clearSend:
                    tvSend.setText("");
                    break;
                case R.id.udp_s_btn_send:
                    Thread thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!etSend.getText().toString().equals("")){
                                try {
                                    udpServer.send(etSend.getText().toString());
                                    Message message = new Message();
                                    message.what = 2;
                                    message.obj = etSend.getText().toString();
                                    myHandler.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread1.start();
                    break;
            }
        }
    }

    private int getPort(String msg){
        if(msg.equals("")){
            msg = "9898";
        }

        return Integer.parseInt(msg);
    }

    public String getHostIp(){
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()){
                    ia = ias.nextElement();
                    if(ia instanceof Inet6Address){
                        continue;
                    }

                    String ip = ia.getHostAddress();
                    if(!"127.0.0.1".equals(ip) && !ip.startsWith("10.0")){
                        hostIp = ia.getHostAddress();
                        Log.e("zdf", "IP:" + hostIp);
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return hostIp;
    }
}
