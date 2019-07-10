package com.example.androidtest2013.socket;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPClient implements Runnable {
    private String TAG = "zdf";//"UdpClient";
    private final static int udpPort = 9898;
    private final String hostIp = "192.168.0.255";//"255.255.255.255";//"192.168.43.255";
    private static DatagramSocket socket = null;
    private static DatagramPacket packetSend, packetRcv;
    private boolean udpLife = true;
    private byte[] msgRcv = new byte[1024];

    public UDPClient(){
        super();
    }

    public boolean isUdpLife(){
        if(udpLife) return true;

        return false;
    }

    public void setUdpLife(boolean b){
        udpLife = b;
    }

    public String send(String msgSend){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(hostIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        packetSend = new DatagramPacket(msgSend.getBytes(), msgSend.getBytes().length, inetAddress, udpPort);

        try {
            socket.send(packetSend);
            Log.e(TAG, "发送的内容" + msgSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msgSend;
    }

    public void send(byte[] imgBuffer){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(hostIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        packetSend = new DatagramPacket(imgBuffer, imgBuffer.length, inetAddress, udpPort);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.send(packetSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(9898);
            //socket.setSoTimeout(3000);
        } catch (SocketException e) {
            Log.e(TAG, "建立接收数据失败！");
            e.printStackTrace();
        }
        packetRcv = new DatagramPacket(msgRcv, msgRcv.length);
        while (udpLife){
            try {
                Log.e(TAG, "UDP监听");
                socket.receive(packetRcv);
                String rcvMsg = new String(packetRcv.getData(), packetRcv.getOffset(), packetRcv.getLength());
                Log.e(TAG, "收到的消息：" + rcvMsg);
                Intent rcvIntent = new Intent();
                rcvIntent.setAction("udpRcvMsg");
                rcvIntent.putExtra("udpRcvMsg", rcvMsg);
                UdpClientActivity.context.sendBroadcast(rcvIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "UDP监听关闭");
        socket.close();
    }
}
