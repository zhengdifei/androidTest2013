package com.example.androidtest2013.video;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.androidtest2013.R;

public class MainRecActivity extends AppCompatActivity {
    private RevImageThread revImageThread;
    public static ImageView imageView;
    private static Bitmap bitmap;
    private static final int COMPLETED = 0x111;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rec);

        imageView = findViewById(R.id.vd_iv);
        myHandler = new MyHandler();

        revImageThread = new RevImageThread(myHandler);
        new Thread(revImageThread).start();
    }

    public static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == COMPLETED){
                bitmap = (Bitmap) msg.obj;
                imageView.setImageBitmap(bitmap);
                super.handleMessage(msg);
            }
        }
    }
}
