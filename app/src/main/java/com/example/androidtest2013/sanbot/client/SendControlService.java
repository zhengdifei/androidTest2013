package com.example.androidtest2013.sanbot.client;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.Socket;

public class SendControlService implements Runnable {
    private String targetIp = "";
    private boolean isStop = false;
    private Socket socket = null;
    private Handler handler = null;
    private InputStream is;
    private OutputStream os;

    public SendControlService(String targetIp, Handler handler) throws Exception {
        if(!"".equals(targetIp)){
            this.targetIp = targetIp;
            this.handler = handler;
        }else {
            Log.e("zdf", "错误的目标地址，无法创建socket");
            throw new Exception("错误的目标IP:" + targetIp);
        }
    }

    public void send(String msg){
        if(os != null){
            try {
                os.write(msg.getBytes());
                os.flush();
                Log.e("zdf", "发送了消息：" + msg);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("zdf", "客户端发送数据出错");
            }
        }
    }

    public void close(){
        if(socket != null){
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("zdf", "控制服务的socket关闭，发生异常。");
            }
            this.socket = null;
        }
        this.targetIp = "";
    }

    private void handlerException(Exception e){
        e.printStackTrace();
        Message message = Message.obtain();
        message.what = Constants.CONTROL_SOCKET_ERROR;
        handler.sendMessage(message);
    }

    @Override
    public void run() {
        try {
            socket = new Socket(targetIp, Constants.LISTENER_CONTROL_PORT);
            Log.e("zdf", "启动控制sanbot的Socket服务， IP:" + targetIp +", Port:" + Constants.LISTENER_CONTROL_PORT);
            socket.setSoTimeout(Constants.CONTROL_SOCKET_TIMEOUT);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            while (!isStop && socket != null && !socket.isClosed() && !socket.isInputShutdown() && socket.isConnected()){
                byte[] buff = new byte[1024];
                int len = 0;
                try {
                    while ((len = is.read(buff)) != -1){
                        String rcvStr = new String(buff, 0, len);
                        Log.e("zdf", "客户端收到消息:" + rcvStr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (ConnectException e){
            handlerException(e);
        }catch (IOException e) {
            handlerException(e);
        }
    }
}
