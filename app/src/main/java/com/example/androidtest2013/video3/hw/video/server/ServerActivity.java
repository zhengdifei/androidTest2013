package com.example.androidtest2013.video3.hw.video.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidtest2013.R;
import com.example.androidtest2013.video3.hw.video.server.util.Constants;

public class ServerActivity extends Activity {

	private static final String TAG = ServerActivity.class.getCanonicalName();
	private TextView status;
	private TextView ip;
	private Handler uiUpdate = new Handler();
	private VideoStreamingSenderServiceClient videoStreamingSenderClient;
	private ServiceConnection mConnection;
	protected volatile boolean isUpdating;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        //set saved value
        String[] settings =  getConf();
        String fps = settings[0];
        String iscompress = settings[1];
        if(!"".equals(fps)){
        	Constants.defFpsMax = Integer.parseInt(fps);
        }
        if(!"".equals(iscompress)){
        	Constants.isCompress  = Boolean.parseBoolean(iscompress);
        }
        
		mConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder service) {
				Log.d(TAG, "Connected to service " + className);
				VideoStreamingSenderService.LocalBinder binder = (VideoStreamingSenderService.LocalBinder) service;
				videoStreamingSenderClient = new VideoStreamingSenderServiceClient(binder.getService());
				uiUpdate.removeCallbacks(uiUpdateRunnable);
				isUpdating = true;
				uiUpdate.postDelayed(uiUpdateRunnable, 1000);
			}

			public void onServiceDisconnected(ComponentName className) {
				Log.d(TAG, "Disconnecting from service " + className);
				isUpdating = false;
				uiUpdate.removeCallbacks(uiUpdateRunnable);
				videoStreamingSenderClient = null;
			}
		};
		startService();
		setContentView(R.layout.activity_video3_server);
		status = (TextView) findViewById(R.id.vd3_s_tv1);
		ip = (TextView) findViewById(R.id.vd3_s_tv2);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindToService();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.w(TAG, "onStop");
		unbindService(mConnection);
	}

	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}


	public void startService() {
		Intent intent = new Intent(this, VideoStreamingSenderService.class);
		ComponentName componentName = startService(intent);
		if (componentName == null) {
			Toast.makeText(this, "Could not connect to service",
					Toast.LENGTH_SHORT).show();
		} 	
	}

	private void bindToService() {
		Toast.makeText(this, "Connecting to service " + VideoStreamingSenderService.class.getSimpleName(),
				Toast.LENGTH_SHORT).show();
		// Bind to the service
		bindService(new Intent(this, VideoStreamingSenderService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}	
	
	
	private Runnable uiUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			//Log.d(TAG, "Updating server UI");
			status.setText(videoStreamingSenderClient.getStatus());
			ip.setText(videoStreamingSenderClient.getIp());
			if(isUpdating) {
				uiUpdate.removeCallbacks(uiUpdateRunnable);
				uiUpdate.postDelayed(uiUpdateRunnable, 1000);
			}
		}
		
	};
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK){
 
			new AlertDialog.Builder(this)
				.setTitle("Quit")
				.setMessage("Do you want to quit?")
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				}).show();
			
			return true;
		}else{		
			return super.onKeyDown(keyCode, event);
		}
	}
 
 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "onDestroy");
//		unbindService(mConnection);
//		getApplicationContext().unbindService(mConnection);
		
		System.exit(0);
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "Configure");
        menu.add(0, 2, 2, "Exit");
        return super.onCreateOptionsMenu(menu);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.video3_server_alert_dialog_text_entry, null);
            //set saved value
            String[] settings =  getConf();
            String fps = settings[0];
            String iscompress = settings[1];
        	final EditText fps_edit = (EditText)textEntryView.findViewById(R.id.ip_edit);
        	final Spinner iscompress_spinner = (Spinner)textEntryView.findViewById(R.id.iscompress_spinner);
        	
        	final String[] m={"YES","NO"};  
        	//将可选内容与ArrayAdapter连接起来  
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);                
            //设置下拉列表的风格  
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);                
            //将adapter 添加到spinner中  
            iscompress_spinner.setAdapter(adapter);                
            //添加事件Spinner事件监听    
//            iscompress_spinner.setOnItemSelectedListener(new SpinnerSelectedListener());                
            //设置默认值  
            iscompress_spinner.setVisibility(View.VISIBLE);
            
            if(!"".equals(fps)){
            	fps_edit.setText(fps);
            }
            if(!"".equals(iscompress)){
            	iscompress_spinner.setSelection("yes".equalsIgnoreCase(iscompress)? 0:1);
            }
            
            new AlertDialog.Builder(ServerActivity.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.conf_dialog_title)
                .setView(textEntryView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	saveConf(fps_edit.getText().toString(),iscompress_spinner.getSelectedItem().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create().show();
        }
        else if(item.getItemId() == 2){
        	this.finish();
        } 
        return true;
    }

	private String[] getConf(){
		final SharedPreferences settings = getSharedPreferences(Constants.settingTag, 0);
        String fps = settings.getString("fps", "15000");
        String iscompress = settings.getString("iscompress", "YES");
        return new String[]{fps,iscompress};
	}
	private void saveConf(String fps,String iscompress){
		Constants.defFpsMax = Integer.parseInt(fps); 
		Constants.isCompress  = Boolean.parseBoolean(iscompress);
		final SharedPreferences settings = getSharedPreferences(Constants.settingTag, 0);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString("fps", fps);
        editor.putString("iscompress", iscompress);
        editor.commit();
	}
}