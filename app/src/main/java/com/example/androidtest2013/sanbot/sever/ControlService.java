package com.example.androidtest2013.sanbot.sever;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ControlService implements  Runnable{
    private boolean isStop = false;
    private ServerSocket server = null;
    private Handler handler = null;
    private List<ThreadSocket> listSocket = null;

    public ControlService(Handler handler){
        this.handler = handler;
        listSocket = new ArrayList<ThreadSocket>();
    }

    private ThreadSocket getSocket(ServerSocket ss){
        try {
            Socket socket = ss.accept();
            Log.e("zdf", "收到一个连接请求，建立一个socket控制通道");
            return new ThreadSocket(socket);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("zdf", "Server 监听出错");
        }
        return null;
    }

    public void close(){
        for(ThreadSocket ts : listSocket){
            ts.close();
        }

        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("zdf", "控制服务的ServerSocket关闭，发生异常。");
        }
        server = null;
    }

    @Override
    public void run() {
        try {
            Log.e("zdf", "启动Sanbot控制服务的ServerSocket服务， Port:" + Constants.LISTENER_CONTROL_PORT);
            server = new ServerSocket(Constants.LISTENER_CONTROL_PORT);
            while(!isStop && server !=null && !server.isClosed()){
                ThreadSocket threadSocket = getSocket(server);
                if(threadSocket != null){
                    listSocket.add(threadSocket);
                    new Thread(threadSocket).start();
                }
            }
            if(server != null){
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ThreadSocket implements Runnable{
        private Socket socket;
        private InputStream is;
        private OutputStream os;
        private PrintWriter pw;
        private boolean isSocketStop = false;

        public ThreadSocket(Socket socket) throws SocketException {
            this.socket = socket;
            this.socket.setSoTimeout(Constants.CONTROL_SOCKET_TIMEOUT);
            String clientIp = socket.getInetAddress().getHostName();
            Log.e("zdf", "监测到新的客户端接入， IP:" + clientIp);

            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                pw = new PrintWriter(os, true);
            } catch (IOException e) {
                e.printStackTrace();
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
        }

        @Override
        public void run() {
            while (!isSocketStop && socket != null && !socket.isClosed() && !socket.isInputShutdown() && socket.isConnected()){
                byte[] buff = new byte[1024];
                int len = 0;
                try {
                    Log.e("zdf", "开始进入数据读取工作");
                    while ((len = is.read(buff)) != -1){
                        String rcvStr = new String(buff, 0, len);
                        Log.e("zdf", "服务器收到消息:" + rcvStr);

                        if("zdf".equals(rcvStr)){
                            send("receive zdf message.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
