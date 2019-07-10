package com.example.androidtest2013.socket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TcpServer implements Runnable {
    private String TAG = "TcpServer";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private long startTime, endTime;
    private int port = 9999;
    private boolean isListen = true;
    private byte[] buffer = new byte[1500000];
    private int[] secondaryBuffer = new int[1000000];

    public ArrayList<ServerSocketThread> sst = new ArrayList<ServerSocketThread>();

    public TcpServer(int port){
        this.port = port;
    }

    public void setIsListen(boolean b){
        isListen = b;
    }

    public void closeSelf(){
        isListen = false;

    }

    private Socket getSocket(ServerSocket ss){
        try {
            return ss.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "run: 监听超时");
            return null;
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.setSoTimeout(60*1000);
            while (isListen){
                Log.e(TAG, "run: 开始监听");

                Socket socket = getSocket(ss);
                if(socket != null){
                    new ServerSocketThread(socket);
                }
            }

            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerSocketThread extends Thread{
        Socket socket = null;
        private PrintWriter pw;
        private InputStream is = null;
        private OutputStream os = null;
        private DataInputStream dis = null;
        private String ip = null;
        private boolean isRun = true;

        ServerSocketThread(Socket socket){
            this.socket = socket;
            ip = socket.getInetAddress().toString();
            Log.e(TAG, "监测到新的客户端接入， IP:" + ip);

            try {
                socket.setSoTimeout(5000);
                os = socket.getOutputStream();
                is = socket.getInputStream();
                dis = new DataInputStream(is);
                pw = new PrintWriter(os,true);
                start();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String msg){
            pw.println(msg);
            pw.flush();
        }

        /**
         * 处理，解析camera原始数据
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

        @Override
        public void run(){
            sst.add(this);
            Log.e(TAG, "rec image start");
            while (isRun && !socket.isClosed() && !socket.isInputShutdown() && socket.isConnected()){
                try {
                    Log.e("zdf", "server 接收数据时间：" + sdf.format(new Date()));
                    startTime = System.currentTimeMillis();
                    //1. 处理文字消息
                    /*if((rcvLen = is.read(buff)) != -1){
                        rcvMsg = new String(buff, 0, rcvLen);
                        Log.e(TAG, "收到消息：" + rcvMsg);
                        Intent intent = new Intent();
                        intent.setAction("tcpServerReceiver");
                        intent.putExtra("tcpServerReceiver", rcvMsg);
                        TcpServerActivity.context.sendBroadcast(intent);
                        if(rcvMsg.equals("QuitServer")){
                            isRun = false;
                        }
                    }*/
                    //2. 处理图片消息
                    int len = dis.readInt();
                    Log.e(TAG, "len:" + len);
                    int count = 0;
                    while (count != len){
                        count += dis.read(buffer, count, len - count);
                    }

                    if(count != 0){
                        //byte[] 保存成图片
                        /*File file = new File("/sdcard/" + System.currentTimeMillis() + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(buffer, 0, count);
                        outputStream.flush();
                        outputStream.close();*/

                        //bitmap保存成图片
                        /*Bitmap bitmap = processImage(buffer, count, Constants.width, Constants.height);
                        File file = new File("/sdcard/" + System.currentTimeMillis() + ".jpg");
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();*/

                        //发送出去
                        Intent intent = new Intent();
                        intent.setAction("tcpServerReceiver");
                        //intent 数据不能超过40k，否则报错。
                        //intent.putExtra("tcpServerReceiver", buffer);
                        //使用静态图片变量解决问题。
                        TcpServerActivity.buffer = Arrays.copyOfRange(buffer, 0, count);
                        TcpServerActivity.context.sendBroadcast(intent);
                        endTime = System.currentTimeMillis();
                        Log.e("zdf", "耗时： " + (endTime - startTime));
                    }


                }catch (EOFException eof){
                    try {
                        socket.close();
                        sst.remove(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "rec image finish");
            try {
                socket.close();
                sst.clear();
                Log.e(TAG, "断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
