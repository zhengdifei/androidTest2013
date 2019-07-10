package com.example.androidtest2013.socket;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

public abstract class DeviceWaitingSearch extends Thread {

    private static final int DEVICE_FIND_PORT = 9000;
    private static final int RECEIVE_TIME_OUT = 1500;
    private static final int RESPONSE_DEVICE_MAX  = 200;

    private static final byte PACK_TYPE_FIND_DEVICE_REQ_10 = 0x10;
    private static final byte PACK_TYPE_FIND_DEVICE_RSP_11 = 0x11;
    private static final byte PACK_TYPE_FIND_DEVICE_CHK_12 = 0x12;

    private static final byte PACK_DATA_TYPE_DEVICE_NAME_20 = 0x20;
    private static final byte PACK_DATA_TYPE_DEVICE_ROOM_21 = 0x21;

    private Context context;
    private String deviceName, deviceRoom;

    public DeviceWaitingSearch(Context context, String name, String room){
        this.context = context;
        this.deviceName = name;
        this.deviceRoom = room;
    }

    @Override
    public void run(){
        DatagramSocket socket = null;
        try {
            //InetSocketAddress socketAddress = new InetSocketAddress("192.168.3.6", DEVICE_FIND_PORT);
            socket = new DatagramSocket(DEVICE_FIND_PORT);
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            while (true){
                //等待搜索
                Log.e("zdf", "等待消息");
                socket.receive(packet);
                Log.e("zdf", "收到一条消息");
                if(verifySearchData(packet)){
                    byte[] sendData = packData();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    Log.e("zdf", "给主机回复信息");
                    socket.send(sendPacket);
                    socket.setSoTimeout(RECEIVE_TIME_OUT);
                    socket.receive(packet);
                    Log.e("zdf", "等待主机接收确认");
                    if(verifyCheckData(packet)){
                        Log.e("zdf", "确认成功");
                        onDeviceSearched((InetSocketAddress) packet.getSocketAddress());
                        break;
                    }
                    socket.setSoTimeout(0);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onDeviceSearched(InetSocketAddress socketAddress);

    /**
     * 打包响应报文
     * 协议： $ + packType(1) + data(n)
     * type类型： name, room类型，name必须在前面
     * @return
     */
    private byte[] packData(){
        byte[] data = new byte[1024];
        int offset = 0;
        data[offset++] = '$';
        data[offset++] = PACK_TYPE_FIND_DEVICE_RSP_11;

        byte[] temp = getBytesFromType(PACK_DATA_TYPE_DEVICE_NAME_20, deviceName);
        System.arraycopy(temp, 0, data, offset, temp.length);
        offset += temp.length;

        temp = getBytesFromType(PACK_DATA_TYPE_DEVICE_ROOM_21, deviceRoom);
        System.arraycopy(temp, 0, data, offset, temp.length);
        offset += temp.length;

        byte[]  retVal = new byte[offset];
        System.arraycopy(data, 0, retVal, 0, offset);
        return retVal;
    }

    private byte[] getBytesFromType(byte type, String val){
        byte[] retVal = new byte[0];
        if(val != null){
            byte[] valBytes = val.getBytes(Charset.forName("UTF-8"));
            retVal = new byte[5 + valBytes.length];
            retVal[0] = type;
            retVal[1] = (byte) valBytes.length;
            retVal[2] = (byte)(valBytes.length >>8);
            retVal[3] = (byte)(valBytes.length >>16);
            retVal[4] = (byte)(valBytes.length >>24);
            System.arraycopy(valBytes, 0, retVal, 5, valBytes.length);
        }
        return retVal;
    }

    /**
     * 校验搜索数据
     * 协议： $ + packType(1) + sendSeq(4)
     * @param packet
     * @return
     */
    private boolean verifySearchData(DatagramPacket packet){
        Log.e("zdf", "开始检查数据");
        if(packet.getLength() != 6){
            return false;
        }

        byte[] data = packet.getData();
        int offset = packet.getOffset();
        int sendSeq;
        if(data[offset++] != '$' || data[offset++] != PACK_TYPE_FIND_DEVICE_REQ_10){
            return false;
        }
        sendSeq = data[offset++] & 0xFF;
        sendSeq |= (data[offset++] << 8);
        sendSeq |= (data[offset++] << 16);
        sendSeq |= (data[offset++] << 24);
        return sendSeq >= 1 && sendSeq <= 3;
    }

    /**
     * 校验确认数据
     * 协议： $ + packType(1) + sendSeq(4) + deviceIp(n<=15)
     * @param packet
     * @return
     */
    private boolean verifyCheckData(DatagramPacket packet){
        if(packet.getLength() < 6){
            return false;
        }
        byte[] data = packet.getData();
        int offset = packet.getOffset();
        int sendSeq;
        if(data[offset++] != '$' || data[offset++] != PACK_TYPE_FIND_DEVICE_CHK_12){
            return false;
        }
        sendSeq = data[offset++] & 0xFF;
        sendSeq |= (data[offset++] << 8);
        sendSeq |= (data[offset++] << 16);
        sendSeq |= (data[offset++] << 24);
        if(sendSeq < 1 || sendSeq > RESPONSE_DEVICE_MAX){
            return false;
        }
        String ip = new String(data, offset, packet.getLength()-offset, Charset.forName("UTF-8"));
        Log.e("zdf", "ip from host:" + ip);
        return ip.equals(getOwnWifiIp());
    }

    public String getOwnWifiIp(){
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled()){
            return "";
        }
        //需要添加访问wifi的权限
        WifiInfo wifiInfo = wm.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        String ipAddr = int2Ip(ipInt);
        Log.e("zdf", "本机Ip:" + ipAddr);
        return int2Ip(ipInt);
    }

    public static String getOwnWifiIp(Context c){
        WifiManager wm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled()){
            return "";
        }
        //需要添加访问wifi的权限
        WifiInfo wifiInfo = wm.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        String ipAddr = int2Ip(ipInt);
        Log.e("zdf", "本机Ip:" + ipAddr);
        return int2Ip(ipInt);
    }
    //将ip的int方式转换成字符串。
    private static String int2Ip(int i){
        return String.format("%d.%d.%d.%d", i & 0xFF, (i >> 8)&0xFF, (i >> 16)&0xFF, (i >> 24)&0xFF);
    }
}
