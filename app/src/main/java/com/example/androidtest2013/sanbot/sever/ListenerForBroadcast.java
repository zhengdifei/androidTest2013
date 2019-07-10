package com.example.androidtest2013.sanbot.sever;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ListenerForBroadcast implements Runnable{
    private boolean isStop = false;
    private DatagramSocket socket = null;

    public ListenerForBroadcast(){

    }

    //设置是否停止
    public void setStop(boolean stop){
        this.isStop = stop;
    }

    public void close(){
        if(this.socket != null){
            this.socket.close();
            this.socket = null;
        }
    }
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(Constants.LISTENER_BROADCAST_PORT);
            Log.e("zdf", "启动设备被发现的DatagramSocket服务， Port:" + Constants.LISTENER_BROADCAST_PORT);

            //socket.setSoTimeout(Constants.BROADCAST_SOCKET_TIMEOUT);
            while (!isStop){
                byte[] recData = new byte[1024];
                DatagramPacket recPack = new DatagramPacket(recData, recData.length);
                socket.receive(recPack);
                Log.e("zdf", "监听到消息， 对方IP:" + recPack.getAddress().getHostName() + ", Port:" + recPack.getPort());

                String recMsg = new String(recPack.getData(), recPack.getOffset(), recPack.getLength());
                if("hello, zdf".equals(recMsg)){
                    byte[] sendData = "hello, it's me".getBytes();//myHostIp.getBytes();
                    DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length,recPack.getAddress(), recPack.getPort());
                    socket.send(sendPack);
                    Log.e("zdf", "收到bot搜索请求，发送上线申请。");
                }else{
                    Log.e("zdf", "收到异常信息：" + recMsg);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(socket != null){
                socket.close();
                socket = null;
            }
        }
    }
}
