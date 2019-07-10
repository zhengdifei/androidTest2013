package com.example.androidtest2013.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.androidtest2013.R;

public class UIsetActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiset);

        findViewById(R.id.ui_tv_btn).setOnClickListener(this);
        findViewById(R.id.ui_et_btn).setOnClickListener(this);
        findViewById(R.id.ui_btn_btn).setOnClickListener(this);
        findViewById(R.id.ui_rd_btn).setOnClickListener(this);
        findViewById(R.id.ui_chk_btn).setOnClickListener(this);
        findViewById(R.id.ui_tog_btn).setOnClickListener(this);
        findViewById(R.id.ui_pb_btn).setOnClickListener(this);
        findViewById(R.id.ui_dp_btn).setOnClickListener(this);
        findViewById(R.id.ui_iv_btn).setOnClickListener(this);
        findViewById(R.id.ui_sp_btn).setOnClickListener(this);
        findViewById(R.id.ui_ga_btn).setOnClickListener(this);
        findViewById(R.id.ui_is_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.ui_tv_btn:
                intent = new Intent(UIsetActivity.this, TextViewActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_et_btn:
                intent = new Intent(UIsetActivity.this, EditTextActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_btn_btn:
                intent = new Intent(UIsetActivity.this, ButtonTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_rd_btn:
                intent = new Intent(UIsetActivity.this, RadioTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_chk_btn:
                intent = new Intent(UIsetActivity.this, CheckboxTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_tog_btn:
                intent = new Intent(UIsetActivity.this, ToggleButtonTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_pb_btn:
                intent = new Intent(UIsetActivity.this, ProgressBarActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_dp_btn:
                intent = new Intent(UIsetActivity.this, DatePickerTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_iv_btn:
                intent = new Intent(UIsetActivity.this, ImageViewTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_sp_btn:
                intent = new Intent(UIsetActivity.this, SpinnerActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_ga_btn:
                intent = new Intent(UIsetActivity.this, GalleryTestActivity.class);
                startActivity(intent);
                break;
            case R.id.ui_is_btn:
                intent = new Intent(UIsetActivity.this, ImageSwitcherActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
