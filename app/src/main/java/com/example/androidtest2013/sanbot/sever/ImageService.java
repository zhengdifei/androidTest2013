package com.example.androidtest2013.sanbot.sever;

import android.media.audiofx.LoudnessEnhancer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ImageService implements Runnable {
    private String targetIp = "";
    private int targetPort = 0;
    private boolean isStop = false;
    private DatagramSocket socket = null;
    private Handler handler = null;
    private long heartbeatTime = 0;
    private int heartbeat_buffer_num = Constants.IMAGE_HEARTBEAT_BUFFER_NUM;


    public ImageService(Handler handler){
        this.handler = handler;
        heartbeat_buffer_num = Constants.IMAGE_HEARTBEAT_BUFFER_NUM;
    }

    public void send(final byte[] datas){
        if(datas.length > 0 ){
            //Log.e("zdf", "准备发送数据");
            if(!"".equals(targetIp)  && targetPort != 0){
                new Thread(){
                    @Override
                    public void run() {
                        DatagramPacket sendPack = null;
                        try {
                            sendPack = new DatagramPacket(datas, datas.length, InetAddress.getByName(targetIp), targetPort);
                            if(socket==null || socket.isClosed()){
                                Log.e("zdf", "socket断开，无法发送");
                                return;
                            }else{
                                socket.send(sendPack);
                                //Log.e("zdf", "数据发送完毕，目标IP:" + targetIp + ", Port:" + targetPort);
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else {
                Log.e("zdf", "目标地址或者端口有误，目标IP:" + targetIp + ", Port:" + targetPort);
            }
        }else{
            Log.e("zdf", "数据长度为0， 不发送。");
        }
    }

    public void setStop(boolean stop){
        if(stop){
            Log.e("zdf", "停止接收消息");
            handlerException(null);
        }
        this.isStop = stop;
    }

    public void close(){
        if(this.socket != null){
            this.socket.close();
            this.socket = null;
        }

        this.targetIp = "";
        this.targetPort = 0;
    }

    private void handlerException(Exception e){
        if(e != null){
            e.printStackTrace();
        }
        Message message = Message.obtain();
        message.what = Constants.CLIENT_DESTORY_REQ;
        handler.sendMessage(message);
    }
    @Override
    public void run() {
        try {
            Log.e("zdf", "启动摄像头同步的DatagramSocket服务， Port:" + Constants.LISTENER_IMAGE_PORT);
            socket = new DatagramSocket(Constants.LISTENER_IMAGE_PORT);
            socket.setSoTimeout(Constants.IMAGE_SOCKET_TIMEOUT);
            byte[] recData = new byte[1024];
            DatagramPacket recPack = new DatagramPacket(recData, recData.length);
            while (!isStop){
                try{
                    socket.receive(recPack);
                    String msgStr = new String(recPack.getData(), recPack.getOffset(), recPack.getLength());
                    if("hi, zdf connect me".equals(msgStr)){
                        this.targetIp = recPack.getAddress().getHostName();
                        this.targetPort = recPack.getPort();
                        Message message = Message.obtain();
                        message.what = Constants.CLIENT_CONNECT_REQ;
                        handler.sendMessage(message);
                        Log.e("zdf", "接收到连接信息，IP：" + this.targetIp + ", Port:" + this.targetPort);
                    }else if("hi, zdf destory me".equals(msgStr)){
                        setStop(true);
                        Log.e("zdf", "断开数据传输");
                    }else if("1".equals(msgStr)){
                        long nowTime = System.currentTimeMillis();
                        if(heartbeatTime == 0){
                            heartbeatTime = nowTime;
                        }else if((nowTime - heartbeatTime) < Constants.IMAGE_HEARTBEAT_TIMEOUT){
                            Log.e("zdf", "收到一个心跳信息");
                            heartbeatTime = nowTime;
                            heartbeat_buffer_num = Constants.IMAGE_HEARTBEAT_BUFFER_NUM;
                        }else {
                            if(heartbeat_buffer_num == 0){
                                Log.e("zdf", "心跳信息超过事件，断开连接");
                                setStop(true);
                                heartbeatTime = 0;
                            }else{
                                heartbeat_buffer_num -= 1;
                                heartbeatTime = nowTime;
                                Log.e("zdf", "心跳超时缓冲一次");
                            }
                        }
                    }else {
                        Log.e("zdf", "接受无法识别的信号");
                    }
                }catch (SocketTimeoutException e){
                    Log.e("zdf", "socket连接超时");
                }
            }
        } catch (SocketException e) {
            handlerException(e);
        } catch (IOException e) {
            handlerException(e);
        }
    }
}
