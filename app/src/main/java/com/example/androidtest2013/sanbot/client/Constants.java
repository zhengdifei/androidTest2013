package com.example.androidtest2013.sanbot.client;

public class Constants {
    //图像属性
    public static int width = 800;
    public static int height = 480;
    public static int fpsMin = 15000;
    public static int fpsMax = 15000;

    //监听端口
    public static int LISTENER_SEARCH_PORT = 9898;
    public static int LISTENER_BROADCAST_PORT = 9898;
    public static int LISTENER_START_IMAGE_PORT = 9999;
    public static int LISTENER_IMAGE_PORT = 9999;
    public static int LISTENER_CONTROL_PORT = 10000;

    //连接超时
    public static int IMAGE_SOCKET_TIMEOUT = 5000;
    public static int BROADCAST_SOCKET_TIMEOUT = 1000;
    public static int CONTROL_SOCKET_TIMEOUT = 5000;

    public static int SEARCH_NUM = 2;
    public static int REC_NUM = 5;

    //消息标识
    public final static int UPDATE_SANBOT_LIST = 1;
    public final static int FIND_ONE_SANBOT = 3;
    public final static int SEARCH_FINISH = 4;
    public final static int UPDATE_SEARCH_PROGRESS = 5;
    public final static int IMAGE_SOCKET_ERROR = 6;

    public final static int START_SHOW_IMAGE = 2;

    public final static int CONTROL_SOCKET_ERROR = 11;
    //UDP IMAGE 心跳
    public static int IMAGE_HEARTBEAT_TIME = 5000;
}
