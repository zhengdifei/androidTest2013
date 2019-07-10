package com.example.androidtest2013.syscomp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BasicBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "接收到Broadcast, 消息：" + intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
    }
}
