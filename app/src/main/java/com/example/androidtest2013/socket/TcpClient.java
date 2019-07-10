package com.example.androidtest2013.socket;

import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient implements Runnable {
    private String TAG = "TcpClient";
    private String serverIp = "";
    private int serverPort = 9999;
    private OutputStream os;
    private PrintWriter pw;
    private DataOutputStream dos;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[] = new byte[4096];
    private String rcvMsg;
    private int rcvLen;

    public TcpClient(String ip, int port){
        this.serverIp = ip;
        this.serverPort = port;
    }

    public void closeSelf(){
        isRun = false;
    }

    public void send(String msg){
        Log.e(TAG, "send images:" + msg);
        pw.println(msg);
        pw.flush();
    }
    //socket连接，建立stream之后，没有结束符，除非socket.close(), 或者stream.close()。
    // 否则视频图像的传输，多通过DataInputStream,DataOutputStream进行。
    public void send(byte[] datas){
        try {
            if(dos != null){
                Log.e(TAG, "start send images");
                dos.writeInt(datas.length);
                dos.write(datas);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIp, serverPort);
            socket.setSoTimeout(5000);
            os = socket.getOutputStream();
            //pw = new PrintWriter(os, true);
            dos = new DataOutputStream(os);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isRun){
            try {
                rcvLen = dis.read(buff);
                rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                Log.e(TAG, "收到的消息：" + rcvMsg);
                Intent intent = new Intent();
                intent.setAction("tcpClientReceiver");
                intent.putExtra("tcpClientReceiver", rcvMsg);
                TcpClientActivity.context.sendBroadcast(intent);
                if(rcvMsg.equals("QuitClient")){
                    isRun = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            os.close();
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
