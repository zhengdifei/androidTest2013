package com.example.androidtest2013.sanbot.client;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UdpBroadcastSearch  implements Runnable{
    private String myHostIp = "";
    private String broadcastHostIp = "";
    private boolean isStop = false;
    private Map<String, Boolean> devices = new HashMap<String, Boolean>();
    private Handler handler;
    private int searchNum = Constants.SEARCH_NUM;
    private int searchTotal = 0;
    private DatagramSocket socket = null;

    public UdpBroadcastSearch(String hostIp, Handler handler) throws Exception {
        this.myHostIp = hostIp;
        this.handler = handler;
        this.searchNum = Constants.SEARCH_NUM;
        this.searchTotal = 0;
        String[] ipStr = hostIp.split("\\.");
        if(ipStr.length != 4){
            throw new Exception("错误的IP格式：" + hostIp);
        }else {
            broadcastHostIp = ipStr[0] + "." + ipStr[1] + "." + ipStr[2] + "." + "255";
            Log.e("zdf", "广播地址：" + broadcastHostIp);
        }
    }

    //设置是否停止
    public void setStop(boolean stop){
        if(stop){
            Log.e("zdf", "退出搜索");
            Message msg = Message.obtain();
            msg.what = Constants.SEARCH_FINISH;
            handler.sendMessage(msg);
        }
        this.isStop = stop;
    }

    public void close(){
        this.myHostIp = "";
        this.handler = null;
        this.searchNum = Constants.SEARCH_NUM;
        this.searchTotal = 0;
    }

    public Set<String> getDevices(){
        return devices.keySet();
    }

    public void addSearchTotal(){
        searchTotal += 1;
        Message msg = Message.obtain();
        msg.what = Constants.UPDATE_SEARCH_PROGRESS;
        msg.obj = searchTotal;
        handler.sendMessage(msg);
    }
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(Constants.LISTENER_SEARCH_PORT);
            Log.e("zdf", "启动设备发现的DatagramSocket服务， Port:" + Constants.LISTENER_SEARCH_PORT);

            socket.setSoTimeout(Constants.BROADCAST_SOCKET_TIMEOUT);
            while (!isStop){
                //发送广播消息
                byte[] sendData = "hello, zdf".getBytes();
                InetAddress inetAddress = InetAddress.getByName(broadcastHostIp);
                DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length,inetAddress, Constants.LISTENER_BROADCAST_PORT);
                socket.send(sendPack);
                Log.e("zdf", "发送了广播消息， 广播IP:" + broadcastHostIp + ", Port:" + Constants.LISTENER_BROADCAST_PORT);
                //接受广播消息
                byte[] recData = new byte[1024];
                DatagramPacket recPack = new DatagramPacket(recData, recData.length);
                int count = Constants.REC_NUM;
                while (count-- > 0){
                    try{
                        socket.receive(recPack);
                        addSearchTotal();
                        String recMsg = new String(recData, recPack.getOffset(), recPack.getLength());
                        if("hello, it's me".equals(recMsg)){
                            Log.e("zdf", recPack.getAddress().getHostName() + "上线了");
                            devices.put(recPack.getAddress().getHostName(), true);

                            Message msg = Message.obtain();
                            msg.what = Constants.FIND_ONE_SANBOT;
                            msg.obj =  recPack.getAddress().getHostName();
                            handler.sendMessage(msg);
                        }else{
                            Log.e("zdf", "收到异常信息：" + recMsg);
                        }
                    }catch (SocketTimeoutException e){
                        Log.e("zdf", "超时次数：" + count);
                        addSearchTotal();
                        if(count == 100){
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                if(searchNum-- > 0){
                    continue;
                }else{
                    if(!devices.isEmpty()){
                        Message msg = Message.obtain();
                        msg.what = Constants.UPDATE_SANBOT_LIST;
                        msg.obj = devices.keySet();
                        handler.sendMessage(msg);
                    }
                    setStop(true);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            socket.close();
            socket = null;
        }
    }
}
