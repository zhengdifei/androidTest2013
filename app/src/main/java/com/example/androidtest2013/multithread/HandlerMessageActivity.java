package com.example.androidtest2013.multithread;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidtest2013.R;
import com.example.androidtest2013.utils.HttpUtils;

import java.io.InputStream;

public class HandlerMessageActivity extends AppCompatActivity {

    private ImageView iv1;
    private TextView tv1;
    private static String IMAGE_URL = "http://ww4.sinaimg.cn/bmiddle/786013a5jw1e7akotp4bcj20c80i3aao.jpg";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_message);

        iv1 = findViewById(R.id.hm_iv1);
        tv1 = findViewById(R.id.hm_tv);

        // 声明一个等待框以提示用户等待
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示信息");
        dialog.setMessage("正在下载，请稍后……");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);

        findViewById(R.id.hm_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new MyThread()).start();
                dialog.show();
            }
        });

        findViewById(R.id.hm_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = "sendMessage";
                myHandler2.sendMessage(msg);
            }
        });

        findViewById(R.id.hm_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = Message.obtain(myHandler2);
                msg.what = 2;
                msg.obj = "sendToTarget";
                msg.sendToTarget();
            }
        });

        findViewById(R.id.hm_btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandler2.sendEmptyMessage(3);
            }
        });

        findViewById(R.id.hm_btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = Message.obtain();
                msg.what = 4;
                msg.obj = "sendMessageDelayed";
                myHandler2.sendMessageDelayed(msg, 3000);
            }
        });

        findViewById(R.id.hm_btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandler2.sendEmptyMessageDelayed(5, 3000);
            }
        });
    }

    private Handler myHandler = new Handler(){
        // 在Handler中获取消息，重写handleMessage()方法
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Bitmap bitmap = (Bitmap) msg.obj;
                iv1.setImageBitmap(bitmap);
                dialog.dismiss();
            }
        }
    };

    private Handler myHandler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 3 || msg.what == 5){
                tv1.setText("空消息:" + msg.what);
            }else {
                tv1.setText("what:" + msg.what + " " + msg.obj.toString());
            }
        }
    };

    public class MyThread implements Runnable{

        @Override
        public void run() {
            InputStream is = HttpUtils.getInputStream(IMAGE_URL);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Message msg = Message.obtain();
            msg.obj = bitmap;
            msg.what = 1;
            msg.arg1 = 1000;
            myHandler.sendMessage(msg);
        }
    }
}
