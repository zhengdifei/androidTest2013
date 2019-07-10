package com.example.androidtest2013.camera;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.androidtest2013.R;

import java.io.File;

public class TakeVedioTestActivity extends AppCompatActivity {

    private static final String VEDIO_PATH = "/sdcard/sysvedio.3gp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_vedio_test);

        findViewById(R.id.cv_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                File file = new File(VEDIO_PATH);
                if(file.exists()){
                    file.delete();
                }
                Uri uri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.e("zdf", "request code:" + requestCode);
    }
}
