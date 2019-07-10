package com.example.androidtest2013.sanbot.sever;

public class Constants {
    public static int width = 800;
    public static int height = 480;
    public static int fpsMin = 15000;
    public static int fpsMax = 15000;

    public static int LISTENER_BROADCAST_PORT = 9898;
    public static int LISTENER_IMAGE_PORT = 9999;
    public static int LISTENER_CONTROL_PORT = 10000;


    public static int IMAGE_SOCKET_TIMEOUT = 5000;
    public static int CONTROL_SOCKET_TIMEOUT = 5000;
    public static int BROADCAST_SOCKET_TIMEOUT = 1000;

    public final static int CLIENT_CONNECT_REQ = 1;
    public final static int CLIENT_DESTORY_REQ = 2;

    public static int IMAGE_HEARTBEAT_TIMEOUT = 10000;
    public static int IMAGE_HEARTBEAT_BUFFER_NUM = 2;
}
