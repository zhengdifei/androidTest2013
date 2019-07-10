package com.example.androidtest2013.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service8 extends Service {
    private final static String TAG = "Service8";
    private MsgBinder msgBinder = null;
    private static Map<User, List<Message>> map = new HashMap<User, List<Message>>();

    static {
        for(int i=0;i<3;i++){
            User user=new User(i, "jack"+i, "9999999999"+i);
            List<Message> messages=new ArrayList<Message>();
            Message msg=null;
            if(i==0)
            {
                msg=new Message(i, "这两天好吗？", "Jerry", new Date().toGMTString());
                messages.add(msg);
            }else if(i==1)
            {
                msg=new Message(i, "周天去逛街吧！", "Tim", new Date().toGMTString());
                messages.add(msg);
                msg=new Message(i, "好无聊！", "Wesley", new Date().toGMTString());
                messages.add(msg);
            }
            else
            {
                msg=new Message(i, "上次的问题解决了吗？", "Bonnie", new Date().toGMTString());
                messages.add(msg);
                msg=new Message(i, "明天一起吃饭吧？", "Matt", new Date().toGMTString());
                messages.add(msg);
                msg=new Message(i, "在哪里？", "Caroline", new Date().toGMTString());
                messages.add(msg);
            }
            map.put(user, messages);
        }
    }

    public class MsgBinder extends IGetMsg.Stub{

        @Override
        public List<Message> getMes(User u) throws RemoteException {
            for(Map.Entry<User, List<Message>> msgs : map.entrySet()){
                if(msgs.getKey().getUsername().equals(u.getUsername()) && msgs.getKey().getPassword().equals(u.getPassword())){
                    Log.e("zdf", "找到信息了");
                    return msgs.getValue();
                }
            }
            Log.e("zdf", "没有找到信息");
            return map.get(u);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // 仅通过startService()启动服务，所以这个方法返回null即可。
        return msgBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service8 is created!");
        msgBinder = new MsgBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service8 is started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service8 is destroyed.");
        msgBinder = null;
    }
}
