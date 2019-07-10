package com.example.androidtest2013.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
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

import java.lang.ref.WeakReference;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServerActivity extends AppCompatActivity {
    private Button btnConn, btnClose, btnClrRcv, btnClrSend,btnSend;
    private TextView tvRcv, tvSend, tvIp;
    private EditText etSend,etPort;
    private ImageView ivRcv;
    private static TcpServer tcpServer = null;
    public static Context context;
    ExecutorService exec = Executors.newCachedThreadPool();
    private MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private MyBtnClicker myBtnClicker = new MyBtnClicker();
    private int[] secondaryBuffer = new int[1000000];
    public static byte[] buffer = new byte[1500000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_server);

        context = this;
        bindId();
        bindListener();
        bindReceiver();
        init();
    }

    private void init(){
        btnConn.setEnabled(true);
        btnClose.setEnabled(false);
        //btnSend.setEnabled(false);
        tvIp.setText(getHostIp());
    }

    private void bindId(){
        btnConn = findViewById(R.id.tcp_s_btn_conn);
        btnClose = findViewById(R.id.tcp_s_btn_close);
        btnClrRcv = findViewById(R.id.tcp_s_btn_clrrcv);
        btnClrSend = findViewById(R.id.tcp_s_btn_clrsend);
        btnSend = findViewById(R.id.tcp_s_btn_send);

        tvRcv = findViewById(R.id.tcp_s_tv_rcv);
        tvSend = findViewById(R.id.tcp_s_tv_send);
        tvIp = findViewById(R.id.tcp_s_tv_ip);
        etPort = findViewById(R.id.tcp_s_et_port);
        etSend = findViewById(R.id.tcp_s_et_send);

        ivRcv = findViewById(R.id.tcp_s_iv_rcv);
    }

    private void bindListener(){
        btnConn.setOnClickListener(myBtnClicker);
        btnClose.setOnClickListener(myBtnClicker);
        btnClrRcv.setOnClickListener(myBtnClicker);
        btnClrSend.setOnClickListener(myBtnClicker);
        btnSend.setOnClickListener(myBtnClicker);
    }

    private class MyHandler extends Handler{
        private final WeakReference<TcpServerActivity> mActivity;
        public MyHandler(TcpServerActivity activity){
            mActivity = new WeakReference<TcpServerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            TcpServerActivity activity = mActivity.get();
            if(activity != null){
                switch (msg.what){
                    case 1:
                        //tvRcv.append(msg.obj.toString());
                        Log.e("zdf", "start show image");
                        //接收原始Camera预览数据
                        //ivRcv.setImageBitmap(processImage(buffer, 0, Constants.width, Constants.height));
                        ivRcv.setImageBitmap(BitmapFactory.decodeByteArray(buffer, 0, buffer.length));
                        break;
                    case 2:
                        tvSend.append(msg.obj.toString());
                        break;
                }
            }
        }
    }

    /**
     * 当Camera， 从预览中直接传输byte[]数据，如果需要转化成Bitmap，需要进行一定的转化。
     * 下面就是转换的方法。
     * 可以再预览的时候就通过YuvImage进行一定的转换，再转换成Bitmap的byte[],传输，在接收端就可以直接转成Bitmap，
     * 实践测试，这种方式效果更好。
     * @param rgb
     * @param yuv420sp
     * @param width
     * @param height
     */
    private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
                                int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }
    private Bitmap processImage(byte[] buff, int len, int width, int height){
        decodeYUV420SP(secondaryBuffer, buff, width, height);
        return Bitmap.createBitmap(secondaryBuffer, width, height, Bitmap.Config.ARGB_8888);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction){
                case "tcpServerReceiver":
                    Log.e("zdf", "收到广播消息");
                    //String msg = intent.getStringExtra("tcpServerReceiver");
                    //byte[] imgBytes = intent.getByteArrayExtra("tcpServerReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    //message.obj = processImage(buffer, 0, Constants.width, Constants.height);//BitmapFactory.decodeByteArray(imgBytes,0, imgBytes.length);
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    private void bindReceiver(){
        IntentFilter intentFilter = new IntentFilter("tcpServerReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private class MyBtnClicker implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tcp_s_btn_conn:
                    Log.e("zdf","开始连接");
                    btnConn.setEnabled(false);
                    btnClose.setEnabled(true);
                    btnSend.setEnabled(true);
                    tcpServer = new TcpServer(getPort(etPort.getText().toString()));
                    exec.execute(tcpServer);
                    break;
                case R.id.tcp_s_btn_close:
                    tcpServer.closeSelf();
                    btnConn.setEnabled(true);
                    btnClose.setEnabled(false);
                    btnSend.setEnabled(false);
                    break;
                case R.id.tcp_s_btn_clrrcv:
                    tvRcv.setText("");
                    break;
                case R.id.tcp_s_btn_clrsend:
                    tvSend.setText("");
                    break;
                case R.id.tcp_s_btn_send:
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = etSend.getText().toString();
                    myHandler.sendMessage(message);
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            tcpServer.sst.get(0).send(etSend.getText().toString());
                        }
                    });
                    break;
            }
        }
    }

    private int getPort(String msg){
        if(msg.equals("")){
            msg = "9999";
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
