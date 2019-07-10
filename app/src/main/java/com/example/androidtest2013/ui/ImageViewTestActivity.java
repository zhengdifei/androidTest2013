package com.example.androidtest2013.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.androidtest2013.R;
import com.example.androidtest2013.utils.HttpUtils;

import java.io.InputStream;
import java.util.Random;

public class ImageViewTestActivity extends AppCompatActivity {
    private final static String URL_PATH = "http://ww4.sinaimg.cn/bmiddle/9e58dccejw1e6xow22oc6j20c80gyaav.jpg";
    private int minWidth = 80;
    private Matrix matrix = new Matrix();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_test);

        final ImageView iv3 = findViewById(R.id.iv3);
        final TextView iv_tv1 = findViewById(R.id.iv_tv1);
        final TextView iv_tv2 = findViewById(R.id.iv_tv2);
        SeekBar iv_sb1 = findViewById(R.id.iv_sb1);
        SeekBar iv_sb2 = findViewById(R.id.iv_sb2);

        //获取当前屏幕的尺寸，并设置图片放大的最大尺寸，不能超过屏幕尺寸
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        iv_sb1.setMax(dm.widthPixels - minWidth);

        iv_sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int newWidth = i + minWidth;
                int newHeight = (int)(newWidth*3/4);
                iv3.setLayoutParams(new LinearLayout.LayoutParams(newWidth, newHeight));
                iv_tv1.setText("图像宽度: " + newWidth + " 图像高度：" + newHeight);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        iv_sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image1);
                matrix.setRotate(i, 30, 60);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                iv3.setImageBitmap(bitmap);
                iv_tv2.setText("progress:" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button iv_btn1 = findViewById(R.id.iv_btn1);
        final TextView iv_tv3 = findViewById(R.id.iv_tv3);
        final ImageView iv4 = findViewById(R.id.iv4);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap = null;
                if(msg.what == 0){
                    bitmap = (Bitmap) msg.obj;
                    iv_tv3.setText("使用obj传递数据");
                }else {
                    Bundle ble = msg.getData();
                    bitmap = (Bitmap)ble.get("bmp");
                    iv_tv3.setText("使用Bundle传递数据");
                }
                iv4.setImageBitmap(bitmap);
            }
        };

        iv_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_tv3.setText("");
                iv4.setImageBitmap(null);
                new Thread(){
                    @Override
                    public void run() {
                        InputStream inputStream = HttpUtils.getInputStream(URL_PATH);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Message msg = new Message();
                        Random rd = new Random();
                        int ird = rd.nextInt(10);
                        Log.e("zdf", "int:" + ird);
                        if(ird / 2 == 0){
                            msg.what = 0;
                            msg.obj = bitmap;
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("bmp", bitmap);
                            msg.what = 1;
                            msg.setData(bundle);
                        }

                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });
    }
}
