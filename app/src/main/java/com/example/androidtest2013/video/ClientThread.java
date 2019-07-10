package com.example.androidtest2013.video;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientThread implements Runnable {
    private static Socket socket;
    private static ByteArrayOutputStream outputStream;
    private static byte[] byteBuffer = new byte[1024];
    public static Camera.Size size;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //向UI线程发送消息
    private Handler handler;

    //接受UI线程消息
    public MyHandler revHandler;

    BufferedReader br = null;
    static OutputStream os = null;

    public ClientThread(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        revHandler = new MyHandler();
        Looper.loop();
    }

    public static class MyHandler extends Handler{
        private static long startTime, endTime;
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x111){
                try {
                    startTime = System.currentTimeMillis();
                    Log.e("zdf", "start connect: " + sdf.format(new Date()));
                    socket = new Socket("192.168.0.115", 40000);
                    if(socket != null){
                        byte[] datas = (byte[]) msg.obj;
                        if(socket.isOutputShutdown()){
                            socket.getKeepAlive();
                        }else {
                            os = socket.getOutputStream();
                            outputStream = new ByteArrayOutputStream();
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(datas);
                            int amount;
                            while ((amount = inputStream.read(byteBuffer)) != -1){
                                os.write(byteBuffer, 0, amount);
                            }
                            outputStream.flush();
                            outputStream.close();
                            os.flush();
                            os.close();
                            socket.close();
                        }
                    }
                    endTime = System.currentTimeMillis();
                    Log.e("zdf", "耗时：" + (endTime - startTime));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
