package com.example.androidtest2013.utils;

import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SomeUtils {
    public static String getHostIp(){
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()){
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()){
                    ia = ias.nextElement();
                    if(ia instanceof Inet6Address){
                        continue;
                    }

                    String ip = ia.getHostAddress();
                    if(!"127.0.0.1".equals(ip) && !ip.startsWith("10.0")){
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return hostIp;
    }
}
