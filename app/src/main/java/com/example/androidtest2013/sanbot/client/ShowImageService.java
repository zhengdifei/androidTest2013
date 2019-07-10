package com.example.androidtest2013.sanbot.client;

import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ShowImageService implements Runnable {
    private String myHostIp = "";
    private boolean isStop = false;
    private String targetIp = "";
    private DatagramSocket socket = null;
    private Handler handler = null;
    private long heartbeatTime = 0;

    public ShowImageService(String targetIp, Handler handler) throws Exception {
        if(!"".equals(targetIp)){
            this.targetIp = targetIp;
            this.handler = handler;
        }else {
            Log.e("zdf", "错误的目标地址，无法创建socket");
            throw new Exception("错误的目标IP:" + targetIp);
        }
    }

    public void send(final byte[] datas){
        if(datas.length > 0 ){
            Log.e("zdf", "准备发送数据");
            if(!"".equals(targetIp)  && Constants.LISTENER_IMAGE_PORT != 0){
                new Thread(){
                    @Override
                    public void run() {
                        DatagramPacket sendPack = null;
                        try {
                            sendPack = new DatagramPacket(datas, datas.length, InetAddress.getByName(targetIp), Constants.LISTENER_IMAGE_PORT);
                            if(socket==null || socket.isClosed()){
                                Log.e("zdf", "socket断开，无法发送");
                                return;
                            }else{
                                socket.send(sendPack);
                                Log.e("zdf", "数据发送完毕，目标IP:" + targetIp + ", Port:" + Constants.LISTENER_IMAGE_PORT);
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else {
                Log.e("zdf", "目标地址或者端口有误，目标IP:" + targetIp + ", Port:" + Constants.LISTENER_IMAGE_PORT);
            }
        }else{
            Log.e("zdf", "数据长度为0， 不发送。");
        }
    }

    public void close(){
        if(socket != null){
            send("hi, zdf destory me".getBytes());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.socket.close();
            this.socket = null;
        }
        this.targetIp = "";
        this.heartbeatTime = 0;
    }

    private void handlerException(Exception e){
        e.printStackTrace();
        Message message = Message.obtain();
        message.what = Constants.IMAGE_SOCKET_ERROR;
        handler.sendMessage(message);
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(Constants.LISTENER_START_IMAGE_PORT);
            Log.e("zdf", "启动显示sanbot摄像头同步的DatagramSocket服务， Port:" + Constants.LISTENER_START_IMAGE_PORT);

            socket.setSoTimeout(Constants.IMAGE_SOCKET_TIMEOUT);
            byte[] sendData = new byte[1024];
            sendData = "hi, zdf connect me".getBytes();
            DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(targetIp), Constants.LISTENER_IMAGE_PORT);
            socket.send(sendPack);
            heartbeatTime = System.currentTimeMillis();
            byte[] recData = new byte[1500000];
            DatagramPacket recPack = new DatagramPacket(recData,recData.length);
            while (!isStop){
                socket.receive(recPack);
                SanbotClientActivity.imageBuff = Arrays.copyOfRange(recPack.getData(), recPack.getOffset(), recPack.getLength());
                Message message = Message.obtain();
                message.what = Constants.START_SHOW_IMAGE;
                handler.sendMessage(message);

                //返回标识
                long nowTime = System.currentTimeMillis();
                if(( nowTime - heartbeatTime) > Constants.IMAGE_HEARTBEAT_TIME){
                    sendPack = new DatagramPacket(new byte[]{'1'}, 0, 1,recPack.getAddress(), recPack.getPort());
                    socket.send(sendPack);
                    heartbeatTime = nowTime;
                    Log.e("zdf", "发送心跳消息");
                }
            }
        } catch (SocketTimeoutException e){
            handlerException(e);
        }catch (SocketException e) {
            handlerException(e);
        } catch (UnknownHostException e) {
            handlerException(e);
        } catch (IOException e) {
            handlerException(e);
        }
    }
}