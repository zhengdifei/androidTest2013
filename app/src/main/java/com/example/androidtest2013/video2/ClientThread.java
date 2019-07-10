package com.example.androidtest2013.video2;

import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ClientThread implements Runnable {
    private static Socket socket ;
    private static ByteArrayOutputStream outputstream;
    private static byte byteBuffer[] = new byte[1024];
    public static Size size;

    //向UI线程发送消息
    private Handler handler;

    //接受UI线程消息
    public MyHandler revHandler;

    BufferedReader br= null;
    static OutputStream os = null;

    public ClientThread(Handler handler){
        this.handler=handler;

    }

    @Override
    public void run() {
        Looper.prepare();
        //接受UI发来的信息
        revHandler = new MyHandler();
        Looper.loop();

    }

    public static class MyHandler extends Handler{
        private Socket s;

        public MyHandler(){
            /*try {

            } catch (IOException e) {
                LoLog.e("zdf", "start connect");
                socket = new Socket("192.168.0.106",19999);
                Log.e("zdf", "connect success");g.e("zdf", "connect fail");
                e.printStackTrace();
            }*/
        }
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0x111){
                try {
                    //传递byte数组，并将数据保存成图片。
                    /*byte[] datas = (byte[]) msg.obj;
                    File file = new File("/sdcard/" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(datas);
                    outputStream.flush();
                    outputStream.close();*/
                    //当传递YuvImage对象，并进行compressToJpeg，常出现内存不足的现象，
                    //https://blog.csdn.net/q979713444/article/details/80446404
                    //系统级的bug，建议使用第三方的图片压缩方法。
                    /*image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outputStream);*/

                    //使用socket进行传输byte[]
                    Log.e("zdf", "start connect");
                    s = new Socket("192.168.0.115",19999);
                    Log.e("zdf", "connect success");
                    if(s != null){
                        byte[] datas = (byte[]) msg.obj;
                        if(s.isOutputShutdown()){
                            s.getKeepAlive();
                        }else {
                            os = s.getOutputStream();
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(datas);
                            int amount;
                            while ((amount = inputStream.read(byteBuffer)) != -1){
                                os.write(byteBuffer, 0, amount);
                            }
                            os.write("\n".getBytes());
                            os.flush();
                            os.close();
                            socket.close();
                        }
                    }
                    //使用socket进行传输YuvImage对象，容易出现内存无法回收的bug
                    /* if(socket.isOutputShutdown()){
                        socket.getKeepAlive();

                    }else{
                        os = socket.getOutputStream();
                        outputstream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outputstream);
                        ByteArrayInputStream inputstream = new ByteArrayInputStream(outputstream.toByteArray());
                        int amount;
                        while ((amount = inputstream.read(byteBuffer)) != -1) {
                            os.write(byteBuffer, 0, amount);
                        }
                        os.write("\n".getBytes());
                        outputstream.flush();
                        outputstream.close();
                        os.flush();
                        os.close();
                        socket.close();
                    }*/
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
