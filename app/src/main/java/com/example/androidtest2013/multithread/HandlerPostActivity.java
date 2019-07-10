package com.example.androidtest2013.multithread;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidtest2013.R;
import com.example.androidtest2013.utils.HttpUtils;

import java.io.InputStream;

public class HandlerPostActivity extends AppCompatActivity {
    private TextView tv;
    private ImageView iv;
    private ProgressDialog dialog;

    private static String IMAGE_URL = "http://ww4.sinaimg.cn/bmiddle/786013a5jw1e7akotp4bcj20c80i3aao.jpg";

    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_post);

        tv = findViewById(R.id.hp_tv1);
        iv = findViewById(R.id.hp_iv);

        findViewById(R.id.hp_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 新启动一个子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // tvMessage.setText("...");
                        // 以上操作会报错，无法再子线程中访问UI组件，UI组件的属性必须在UI线程中访问
                        // 使用post方式修改UI组件tvMessage的Text属性
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText("使用Handler.post在工作线程中发送一段执行到消息队列中，在主线程中执行。");
                            }
                        });
                    }
                }).start();
            }
        });

        findViewById(R.id.hp_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("使用Handler.postDelayed在工作线程中发送一段执行到消息队列中，在主线程中延迟3S执行。");
                    }
                }, 3000);
            }
        });

        // 声明一个等待框以提示用户等待
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示信息");
        dialog.setMessage("正在下载，请稍后……");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);

        findViewById(R.id.hp_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new MyThread()).start();
                dialog.show();
            }
        });
    }

    public class MyThread implements Runnable{

        @Override
        public void run() {
            InputStream is = HttpUtils.getInputStream(IMAGE_URL);
            final Bitmap bitmap = BitmapFactory.decodeStream(is);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    iv.setImageBitmap(bitmap);
                }
            });
            dialog.dismiss();
        }
    }
}
