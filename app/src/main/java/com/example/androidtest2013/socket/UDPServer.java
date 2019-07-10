package com.example.androidtest2013.socket;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class UDPServer implements Runnable{
    private String TAG = "zdf";//"UDPServer"
    private String ip = null;
    private int port = 9898;
    private DatagramPacket dpRcv = null, dpSend = null;
    private static DatagramSocket ds = null;
    private InetSocketAddress inetSocketAddress = null;
    private byte[] msgRcv = new byte[10000000];
    private boolean udpLife = true;
    private boolean udpLifeOver = true;

    public UDPServer(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    private void setSoTime(int ms) throws SocketException {
        ds.setSoTimeout(ms);
    }

    //返回udp生命线程因子是否存活
    public boolean isUdpLife(){
        if(udpLife) return true;

        return false;
    }
    //返回具体线程生命信息是否完结
    public boolean getLifeMsg(){
        return udpLifeOver;
    }
    //更改UDP生命线程因子
    public void setUdpLife(boolean b){
        udpLife = b;
    }

    public void send(String sendStr) throws IOException {
        Log.e(TAG, "客户端IP:" + dpRcv.getAddress().getHostAddress() + ", 客户端Port:" + dpRcv.getPort());
        dpSend = new DatagramPacket(sendStr.getBytes(), sendStr.getBytes().length, dpRcv.getAddress(), dpRcv.getPort());
        ds.send(dpSend);
    }

    @Override
    public void run() {
        //inetSocketAddress = new InetSocketAddress(ip, port);
        try {
            ds = new DatagramSocket(port);

            Log.e(TAG, "UDP服务器("+ ip +")已经启动。");

            //setSoTime(3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
        while (udpLife){
            try {
                Log.e(TAG, "UDP监听中");
                ds.receive(dpRcv);
                //1. 传递文字信息
                String str = new String(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());
                Log.e(TAG, "收到的信息" + str);
                Intent intent = new Intent();
                intent.setAction("udpReceiver");
                intent.putExtra("udpReceiver", str);
                UdpServerActivity.context.sendBroadcast(intent);

                //2. 传递图片数据byte[]
                /*Log.e(TAG, "收到数据");
                UdpServerActivity.imgBuffer = Arrays.copyOfRange(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());

                Intent intent = new Intent();
                intent.setAction("udpReceiver");
                UdpServerActivity.context.sendBroadcast(intent);*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ds.close();
        Log.e(TAG, "UDP监听关闭");
        udpLifeOver = false;
    }
}