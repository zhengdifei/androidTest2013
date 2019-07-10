package com.example.androidtest2013.multithread;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.androidtest2013.R;
import com.example.androidtest2013.utils.HttpUtils;

import java.io.InputStream;

public class AsyncTaskTestActivity extends AppCompatActivity {
    private ImageView iv;
    private static String IMAGE_URL = "http://ww4.sinaimg.cn/bmiddle/786013a5jw1e7akotp4bcj20c80i3aao.jpg";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_test);

        Button btn1 = findViewById(R.id.at_btn1);
        iv = findViewById(R.id.at_iv1);

        // 声明一个等待框以提示用户等待
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示信息");
        dialog.setMessage("正在下载，请稍后……");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyTask().execute(IMAGE_URL);
            }
        });
    }

    // 以String类型的参数，Void表示没有进度信息，Bitmap表示异步任务返回一个位图
    public class MyTask extends AsyncTask<String, Integer, Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //显示等待框
            dialog.show();
        }
        //主要是完成耗时操作
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            publishProgress(0);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = HttpUtils.getInputStream(params[0]);
            bitmap = BitmapFactory.decodeStream(is);
            publishProgress(50);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(100);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            iv.setImageBitmap(bitmap);
            dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }
    }
}
