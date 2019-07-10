package com.example.androidtest2013.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RevImageThread implements Runnable {
    public Socket s;
    public ServerSocket ss;

    //向UI线程发送消息
    private Handler handler;
    private Bitmap bitmap;

    private static final int COMPLETED = 0x111;

    public RevImageThread(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            ss = new ServerSocket(40000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream ins = null;
        while (true){
            try {
                s = ss.accept();
                ins = s.getInputStream();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while((len = ins.read(buffer)) != -1){
                    outputStream.write(buffer, 0, len);
                }
                ins.close();
                byte[] datas = outputStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
                Message msg = handler.obtainMessage();
                msg.what = COMPLETED;
                msg.obj = bitmap;
                handler.sendMessage(msg);

                outputStream.flush();
                outputStream.close();
                if(!s.isClosed()){
                    s.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
