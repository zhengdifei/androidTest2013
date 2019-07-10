package com.example.androidtest2013.sanbot.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidtest2013.R;
import com.example.androidtest2013.utils.SomeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchBotActivity extends AppCompatActivity {
    private ListView lv;
    private TextView tv;
    List<String> listData = new ArrayList<String>();
    Set<String> deviceSet = new HashSet<String>();
    private String myHostIp = SomeUtils.getHostIp();
    private UdpBroadcastSearch udpBroadcastSearch = null;
    private MyHandler myHandler = new MyHandler();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bot);

        bindComp();
        bindListener();
    }

    private void bindComp(){
        lv = findViewById(R.id.sb_c_lv);
        tv = findViewById(R.id.sb_c_tv);
        //搜索提示框
        dialog = new ProgressDialog(this);
        dialog.setTitle("搜索提示");
        dialog.setMessage("正在进行搜索，请等待……");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
    }

    private void bindListener(){
        findViewById(R.id.sb_c_btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchBot();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchBotActivity.this, SanbotClientActivity.class);
                intent.putExtra("targetIp", listData.get(i));
                startActivity(intent);
            }
        });
    }

    private void startSearchBot(){
        if (!"".equals(myHostIp)){
            try {
                Log.e("zdf", "我的IP:" + myHostIp);
                udpBroadcastSearch = new UdpBroadcastSearch(myHostIp, myHandler);
                new Thread(udpBroadcastSearch).start();
                deviceSet = new HashSet<>();
                updateDeviceList(null);
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("zdf", e.getMessage());
            }
        }else {
            Log.e("zdf", "获取不到本地host地址");
        }
    }

    private void updateDeviceList(String oneIp){
        if(oneIp != null){
            deviceSet.add(oneIp);
        }
        listData = new ArrayList<String>(deviceSet);
        //设置提示信息
        if(listData.size() > 0){
            tv.setVisibility(View.GONE);
        }else {
            tv.setVisibility(View.VISIBLE);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchBotActivity.this, android.R.layout.simple_list_item_single_choice, listData);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(adapter);
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.UPDATE_SANBOT_LIST:
                    Set tempSet = (Set<String>) msg.obj;
                    deviceSet = new HashSet<>(tempSet);
                    //设定为单选项
                    updateDeviceList(null);
                    break;
                case Constants.FIND_ONE_SANBOT:
                    String oneIp = (String) msg.obj;
                    Toast.makeText(SearchBotActivity.this, "发现了一个设备：" + oneIp, Toast.LENGTH_SHORT).show();
                    updateDeviceList(oneIp);
                    break;
                case Constants.SEARCH_FINISH:
                    dialog.dismiss();
                    updateDeviceList(null);
                    break;
                case Constants.UPDATE_SEARCH_PROGRESS:
                    int searchTotal = (int) msg.obj;
                    //Log.e("zdf", "进度信息：" + searchTotal*100/((Constants.SEARCH_NUM+1)*Constants.REC_NUM));
                    dialog.setProgress(searchTotal*100/((Constants.SEARCH_NUM+1)*Constants.REC_NUM));
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        udpBroadcastSearch.close();
        Log.e("zdf", "搜索服务退出");
    }
}
