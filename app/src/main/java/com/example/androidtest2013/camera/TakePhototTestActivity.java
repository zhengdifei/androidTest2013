package com.example.androidtest2013.camera;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.androidtest2013.R;

import java.io.File;

public class TakePhototTestActivity extends AppCompatActivity {
    private static final String IMAGE_PATH = "/sdcard/syscamera.jpg";
    private ImageView iv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photot_test);

        iv1 = findViewById(R.id.c_iv1);

        findViewById(R.id.c_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // 指定开启系统相机的Action
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                // 根据文件地址创建文件
                File file = new File(IMAGE_PATH);
                if(file.exists()){
                    file.delete();
                }
                // 把文件地址转换成Uri格式
                Uri uri = Uri.fromFile(file);
                // 设置系统相机拍摄照片完成后图片文件的存放地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 0);
            }
        });
        findViewById(R.id.c_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.e("zdf", "request code:" + requestCode);

        if(requestCode == 0){
            File file = new File(IMAGE_PATH);
            Uri uri = Uri.fromFile(file);
            iv1.setImageURI(uri);
        //红米手机，三星手机，此处存在bug，不管是否指定Uri，都返回null。
        }else if(requestCode == 1){
            Log.e("zdf", "content:" + data.getData());
            iv1.setImageURI(data.getData());
        }else {
            Log.e("zdf", "no request code");
        }
    }
}
