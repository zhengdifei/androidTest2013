package com.example.androidtest2013.multithread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidtest2013.R;

public class LooperTestActivity extends AppCompatActivity {
    private TextView tv1;

    private static MyHandler myHandler;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper_test);

        tv1 =  findViewById(R.id.lp_tv1);

        findViewById(R.id.lp_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandler = new MyHandler();
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = "默认的looper";
                myHandler.sendMessage(msg);
            }
        });

        findViewById(R.id.lp_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Looper looper = Looper.myLooper();
                myHandler = new MyHandler(looper);
                Message msg = Message.obtain();
                msg.what = 2;
                msg.obj = "使用当前线程的looper";
                myHandler.sendMessage(msg);
            }
        });

        findViewById(R.id.lp_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // onClick方法是运行在UI线程上的
                Message message = Message.obtain();
                message.what = 3;
                message.obj = "向子线程发送消息";
                // 向子线程中发送消息
                handler.sendMessage(message);
            }
        });
        // 在UI线程中开启一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在子线程中初始化一个Looper对象
                Looper.prepare();
                handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        // 把UI线程发送来的消息显示到屏幕上。
                        Log.e("zdf", "what:" + msg.what + ", " + msg.obj.toString());
                        //tv1.setText("what:" + msg.what + ", " + msg.obj.toString());
                        Toast.makeText(LooperTestActivity.this, "what:" + msg.what + ", " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                Looper.loop();
            }
        }).start();
    }

    public class MyHandler extends Handler{
        public MyHandler() {
            super();
        }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            tv1.setText(msg.obj.toString());
        }
    }
}
