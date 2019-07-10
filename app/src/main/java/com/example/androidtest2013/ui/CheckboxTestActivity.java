package com.example.androidtest2013.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.androidtest2013.R;

import java.util.ArrayList;
import java.util.List;

public class CheckboxTestActivity extends AppCompatActivity {
    private List<CheckBox> chks = new ArrayList<CheckBox>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_checkbox_test);
        this.context = this;
        chooseMethod(false);
        findViewById(R.id.chk_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "";
                for(CheckBox c : chks){
                    if(c.isChecked()){
                        s += c.getText() + "\n";
                    }
                }
                if("".equals(s)){
                    s = "您没有选中选项！";
                }
                //使用对话框弹出选中的信息
                AlertDialog exit = new AlertDialog.Builder(context).setMessage(s).setPositiveButton("Exit", null).show();
            }
        });
    }

    private void chooseMethod(boolean b){
        String[] checkboxText = new String[]{
                "You are student?",
                "Do you like Android?",
                "Do you have a girlfriend",
                "Do you like online shopping?"
        };

        if(b){
            //使用本文中提到的第一种方式，通过Id动态加载
            //获取带填充的布局控件
            LinearLayout linearLayout = this.findViewById(R.id.checkLayout);

            for(int i =0; i < checkboxText.length; i++){
                CheckBox checkBox = new CheckBox(this);
                chks.add(checkBox);
                chks.get(i).setText(checkboxText[i]);
                linearLayout.addView(checkBox);
            }
        }else {
            //通过动态填充的方式，找到布局文件
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_checkbox_test, null);
            for(int i =0;i < checkboxText.length; i++){
                CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.checkbox_layout, null);
                chks.add(checkBox);
                chks.get(i).setText(checkboxText[i]);
                linearLayout.addView(checkBox);
            }
            setContentView(linearLayout);
        }
    }
}
