package com.example.androidtest2013.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.androidtest2013.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);

        Spinner sp1 = findViewById(R.id.sp1);
        Spinner sp2 = findViewById(R.id.sp2);
        // 声明一个ArrayAdapter用于存放简单数据
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SpinnerActivity.this, android.R.layout.simple_spinner_item, getData()
        );
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SpinnerActivity.this, adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner sp5 = findViewById(R.id.sp5);
        SimpleAdapter simpleAdapter = new SimpleAdapter(SpinnerActivity.this,getAdpaterData(), R.layout.spinner_adapter, new String[]{"ivLogo", "appName"}, new int[]{R.id.imageview, R.id.textview});
        sp5.setAdapter(simpleAdapter);
        sp5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> map = (Map<String, Object>) adapterView.getItemAtPosition(i);
                Toast.makeText(SpinnerActivity.this, map.get("appName").toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ArrayList<String> getData(){
        ArrayList<String> list = new ArrayList<>();
        list.add("180平米的房子");
        list.add("一个勤劳漂亮的老婆");
        list.add("一辆宝马");
        list.add("一个强壮且永不生病的身体");
        list.add("一个喜欢的事业");
        return list;
    }

    private List<Map<String, Object>> getAdpaterData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ivLogo", R.drawable.a1);
        map.put("appName", "图标1");
        list.add(map);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("ivLogo", R.drawable.a2);
        map2.put("appName", "图标2");
        list.add(map2);

        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("ivLogo", R.drawable.a3);
        map3.put("appName", "图标3");
        list.add(map3);

        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("ivLogo", R.drawable.a4);
        map4.put("appName", "图标4");
        list.add(map4);
        return  list;
    }
}
