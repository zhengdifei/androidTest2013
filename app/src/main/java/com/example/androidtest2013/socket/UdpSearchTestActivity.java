package com.example.androidtest2013.socket;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.androidtest2013.R;

import java.net.InetSocketAddress;

public class UdpSearchTestActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_serach_test);

        context = this;
        findViewById(R.id.udp_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zdf", "开启被搜索功能, 本机Ip:" + DeviceWaitingSearch.getOwnWifiIp(context));
                new DeviceWaitingSearch(context, "日灯光", "灯光") {

                    @Override
                    public void onDeviceSearched(InetSocketAddress socketAddress) {
                        //UI操作
                        Log.e("zdf", "搜索主机：" + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort());
                    }
                }.start();
            }
        });
    }
}
