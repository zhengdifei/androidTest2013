package com.example.androidtest2013.video2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.ServerSocket;

import com.example.androidtest2013.R;

public class MainRec2Activity extends Activity  {
    RevImageThread revImageThread;
    public static ImageView image;
    private static Bitmap bitmap;
    private static final int COMPLETED = 0x111;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rec2);

        image=(ImageView)findViewById(R.id.imageView1);
        handler = new MyHandler();
        revImageThread = new RevImageThread(handler);
        new Thread(revImageThread).start();
    }

    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if (msg.what == COMPLETED) {
                bitmap = (Bitmap)msg.obj;
                image.setImageBitmap(bitmap);
                super.handleMessage(msg);
            }
        }
    }
}
