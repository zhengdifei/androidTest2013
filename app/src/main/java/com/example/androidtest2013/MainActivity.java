package com.example.androidtest2013;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.androidtest2013.app.MyApp;
import com.example.androidtest2013.app.MyCopyData;
import com.example.androidtest2013.camera.CameraSetActivity;
import com.example.androidtest2013.intent.IntentTestActivity;
import com.example.androidtest2013.multithread.AsyncTaskTestActivity;
import com.example.androidtest2013.multithread.HandlerMessageActivity;
import com.example.androidtest2013.multithread.HandlerPostActivity;
import com.example.androidtest2013.multithread.LooperTestActivity;
import com.example.androidtest2013.sanbot.client.SearchBotActivity;
import com.example.androidtest2013.sanbot.sever.SanbotMainActivity;
import com.example.androidtest2013.service.ServiceSetActivity;
import com.example.androidtest2013.socket.SocketSetActivity;
import com.example.androidtest2013.socket.UdpSearchTestActivity;
import com.example.androidtest2013.syscomp.BroadcastTestActivity;
import com.example.androidtest2013.video.MainRecActivity;
import com.example.androidtest2013.video.MainVideoActivity;
import com.example.androidtest2013.video3.hw.video.client.ClientActivity;
import com.example.androidtest2013.video3.hw.video.server.ServerActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {
    private MyApp myApp;

    private MyBtnClick myBtnClick = new MyBtnClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApp = (MyApp) getApplication();
        Log.e("zdf", "show app value :" + myApp.getName());

        findViewById(R.id.app_value_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.setName("no body");
                Log.e("zdf", "app value set:" + myApp.getName());
            }
        });

        //剪切板
        findViewById(R.id.copy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //cm.getPrimaryClip(ClipData.newPlainText("data", "copy zdf"));
                ClipData cd = cm.getPrimaryClip();
                Log.e("zdf", cd.getItemAt(0).getText().toString());
                cm.setPrimaryClip(ClipData.newPlainText("data", "copy zdf"));
            }
        });

        //自定义剪切板
        findViewById(R.id.self_copy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String baseToStr = "";
                MyCopyData myCopyData = new MyCopyData("zdf", 30);
                ByteArrayOutputStream bArr = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(bArr);
                    oos.writeObject(myCopyData);
                    baseToStr = Base64.encodeToString(bArr.toByteArray(), Base64.DEFAULT);
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cm.setPrimaryClip(ClipData.newPlainText("data", baseToStr));
            }
        });

        //获取自定义剪切板数据
        findViewById(R.id.get_self_copy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cd = cm.getPrimaryClip();
                String msg = cd.getItemAt(0).getText().toString();
                byte[] base64_byte = Base64.decode(msg, Base64.DEFAULT);
                ByteArrayInputStream bais = new ByteArrayInputStream(base64_byte);
                try {
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    MyCopyData myCopyData = (MyCopyData) ois.readObject();
                    Log.e("zdf", "get self copy data:" + myCopyData.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        //通过Intent传递数据
        findViewById(R.id.jump_intent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IntentTestActivity.class);
                intent.putExtra("data", "主页面传递的数据");
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.main_btn_at).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_hm).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_hp).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_lp).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_br).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_upd_br).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_sk).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_sv).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_cam).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_vdRec).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_vdSend).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_vd3Rec).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_vd3Send).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_sbServer).setOnClickListener(myBtnClick);
        findViewById(R.id.main_btn_sbClient).setOnClickListener(myBtnClick);
    }

    private class MyBtnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case R.id.main_btn_at:
                    intent = new Intent(MainActivity.this, AsyncTaskTestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_hp:
                    intent = new Intent(MainActivity.this, HandlerPostActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_hm:
                    intent = new Intent(MainActivity.this, HandlerMessageActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_lp:
                    intent = new Intent(MainActivity.this, LooperTestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_br:
                    intent = new Intent(MainActivity.this, BroadcastTestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_upd_br:
                    intent = new Intent(MainActivity.this, UdpSearchTestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_sk:
                    intent = new Intent(MainActivity.this, SocketSetActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_sv:
                    intent = new Intent(MainActivity.this, ServiceSetActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_cam:
                    intent = new Intent(MainActivity.this, CameraSetActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_vdSend:
                    intent = new Intent(MainActivity.this, MainVideoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_vdRec:
                    intent = new Intent(MainActivity.this, MainRecActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_vd3Send:
                    intent = new Intent(MainActivity.this, ServerActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_vd3Rec:
                    intent = new Intent(MainActivity.this, ClientActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_sbServer:
                    intent = new Intent(MainActivity.this, SanbotMainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_btn_sbClient:
                    intent = new Intent(MainActivity.this, SearchBotActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
