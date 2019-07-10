package com.example.androidtest2013.sanbot.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidtest2013.R;
import com.example.androidtest2013.socket.SocketSetActivity;

public class SanbotClientActivity extends AppCompatActivity {
    private Button upBtn, downBtn, rightBtn, leftBtn, connBtn, destoryBtn, sendBtn;
    private ImageView iv;
    private EditText et;
    private MyClicker myClicker = new MyClicker();
    private MyHandler myHandler = new MyHandler();
    private ShowImageService showImageService = null;
    private SendControlService sendControlService = null;
    private String targetIp = "";
    public static byte[] imageBuff = null;
    private boolean connMark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanbot_client);

        init();
        bindComp();
        bindListener();
        unControlState();
    }

    private void init(){
        Intent intent = getIntent();
        String targetIp = intent.getStringExtra("targetIp");
        if("".equals(targetIp)){
            Log.e("zdf", "错误的目标地址：" + targetIp);
            intent = new Intent(SanbotClientActivity.this, SearchBotActivity.class);
            startActivity(intent);
            finish();
        }else{
            Log.e("zdf", "设置目标主机：" + targetIp);
            this.targetIp = targetIp;
        }
    }

    private void bindComp(){
        iv = findViewById(R.id.sb_c_iv);
        upBtn = findViewById(R.id.sb_c_btn_up);
        downBtn = findViewById(R.id.sb_c_btn_down);
        leftBtn = findViewById(R.id.sb_c_btn_left);
        rightBtn = findViewById(R.id.sb_c_btn_right);
        connBtn = findViewById(R.id.sb_c_btn_conn);
        destoryBtn = findViewById(R.id.sb_c_btn_destroy);
        sendBtn = findViewById(R.id.sb_c_btn_send);

        et = findViewById(R.id.sb_c_et);
    }

    private void bindListener(){
        upBtn.setOnClickListener(myClicker);
        downBtn.setOnClickListener(myClicker);
        rightBtn.setOnClickListener(myClicker);
        leftBtn.setOnClickListener(myClicker);
        connBtn.setOnClickListener(myClicker);
        destoryBtn.setOnClickListener(myClicker);
        sendBtn.setOnClickListener(myClicker);
    }

    private void startConn(){
        Log.e("zdf", "连接目标主机：" + targetIp);
        try {
            //发送图片服务
            showImageService = new ShowImageService(targetIp, myHandler);
            new Thread(showImageService).start();
            //发送控制服务
            sendControlService = new SendControlService(targetIp, myHandler);
            new Thread(sendControlService).start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("zdf", e.getMessage());
        }
    }

    private void stopConn(){
        Log.e("zdf", "断开目标主机：" + targetIp);
        //关闭图像服务
        if(showImageService != null){
            showImageService.close();
            showImageService = null;
        }
        //关闭控制服务
        if(sendControlService != null){
            sendControlService.close();
            sendControlService = null;
        }

        imageBuff = null;
        startPreview();
    }

    private void startPreview(){
        if(imageBuff == null){
            Log.e("zdf", "图片缓存为空，无法展示图片");
            iv.setImageResource(R.drawable.image1);
        }else {
           // Log.e("zdf", "展示一张图片");
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBuff, 0 , imageBuff.length);
            //传输过来的图片是横向的，设置旋转90度，但是会牺牲一些数据预览的性能，有滞后感。
            //可用在传输之前就进行旋转。
           /* Matrix matrix = new Matrix();
            matrix.setRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            iv.setImageBitmap(rotatedBitmap);*/
           iv.setImageBitmap(bitmap);
        }
    }

    /**
     * 是否连接标识
     * @param b
     */
    private void setConnMark(boolean b){
        connMark = b;
        if(connMark){
            startConn();
            controlState();
        }else{
            stopConn();
            unControlState();
        }
    }

    private void sendMsg(){
        String msg = et.getText().toString();
        if("".equals(msg)){
            Log.e("zdf", "空文本消息，不发送");
        }else {
            if(showImageService != null){
                sendControlService.send(msg);
            }else{
                Log.e("zdf", "控制发送服务为空，无法发送");
            }
        }
    }

    private void controlState(){
        connBtn.setEnabled(false);
        destoryBtn.setEnabled(true);
        upBtn.setEnabled(true);
        downBtn.setEnabled(true);
        leftBtn.setEnabled(true);
        rightBtn.setEnabled(true);
        sendBtn.setEnabled(true);
    }

    private void unControlState(){
        connBtn.setEnabled(true);
        destoryBtn.setEnabled(false);
        upBtn.setEnabled(false);
        downBtn.setEnabled(false);
        leftBtn.setEnabled(false);
        rightBtn.setEnabled(false);
        sendBtn.setEnabled(false);
    }

    private class MyClicker implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sb_c_btn_up:
                    break;
                case R.id.sb_c_btn_down:
                    break;
                case R.id.sb_c_btn_left:
                    break;
                case R.id.sb_c_btn_right:
                    break;
                case R.id.sb_c_btn_conn:
                    setConnMark(true);
                    break;
                case R.id.sb_c_btn_destroy:
                    setConnMark(false);
                    break;
                case R.id.sb_c_btn_send:
                    sendMsg();
                    break;
                default:
                    break;
            }
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.START_SHOW_IMAGE:
                    startPreview();
                    break;
                case Constants.IMAGE_SOCKET_ERROR:
                    setConnMark(false);
                    break;
                case Constants.CONTROL_SOCKET_ERROR:
                    Toast.makeText(SanbotClientActivity.this, "控制服务异常，暂时无法提供控制服务", Toast.LENGTH_SHORT).show();
                    Log.e("zdf", "控制服务异常，暂时无法提供控制服务");
                    sendBtn.setEnabled(false);
                    sendControlService = null;
                    break;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setConnMark(connMark);
        Log.e("zdf", "服务控制恢复，机器人服务恢复预览");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(showImageService != null) showImageService.close();
        Log.e("zdf", "服务控制进入后台，停止预览功能");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("zdf", "机器人控制服务退出");
    }
}
