package com.example.androidtest2013.video3.hw.video.client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidtest2013.R;
import com.example.androidtest2013.video3.hw.video.client.util.Constants;

public class ClientActivity extends Activity {
	protected static final String TAG = ClientActivity.class.getCanonicalName();
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition isPausedCondition = lock.newCondition();
	private volatile boolean isPaused = true;
	private ImageView imageView;
	private Handler imageUpdaterHandler = new Handler();
	private VideoStreamingReceiverClient videoStreamingReceiverClient;
	private long startTime;
	
	private void changePauseState(boolean flag) {
		lock.lock();
		try {
			isPaused = flag;
			imageUpdaterHandler.removeCallbacks(imageUpdaterRunnable);
			if(!flag) {
				displayed = 0;
				underflow = 0;
				startTime = System.currentTimeMillis();
				imageUpdaterHandler.postDelayed(imageUpdaterRunnable, 200);
			}
			isPausedCondition.signal();
		} finally {
			lock.unlock();
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			Toast.makeText(ClientActivity.this, "Service connected " + className,
					Toast.LENGTH_SHORT).show();
			VideoStreamingReceiverService.LocalBinder binder = (VideoStreamingReceiverService.LocalBinder) service;
			videoStreamingReceiverClient = new VideoStreamingReceiverClient(binder.getService());
			changePauseState(false);
		}

		public void onServiceDisconnected(ComponentName className) {
			Toast.makeText(ClientActivity.this, "Service disconnected " + className,
					Toast.LENGTH_SHORT).show();			
			changePauseState(true);
		}
	};
	private TextView textViewStatus;
	protected int displayed;
	protected int underflow;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//获取配置
		String[] settings =  getConf();
        String ip = settings[0];
        if(!"".equals(ip)){
            String port = settings[1];
            Constants.serverIP = ip;
            Constants.serverPort = Integer.parseInt(port);
        }	
        
        //startService(); 
        setContentView(R.layout.activity_video3_client);
        imageView = (ImageView) findViewById(R.id.vd3_c_iv);
        textViewStatus = (TextView) findViewById(R.id.vd3_c_tv1);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
        bindService();	
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mConnection);
	}
	
	private void startService() {
		Intent intent = new Intent(this, VideoStreamingReceiverService.class);
		//we start the service with the intent to make sure that it always
		//runs in the background even if we unbind from the service.
		ComponentName componentName = startService(intent);
		if (componentName == null) {
			Toast.makeText(this, "Could not connect to service",
					Toast.LENGTH_SHORT).show();
		} 	
	}

	private void bindService() {
		Toast.makeText(this, "Connecting to service " + VideoStreamingReceiverService.class.getName(),
				Toast.LENGTH_SHORT).show();
		// Bind to the service
		bindService(new Intent(this, VideoStreamingReceiverService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}

    
	private Runnable imageUpdaterRunnable = new Runnable() {
		@Override
		public void run() {
			//Log.d(TAG, "Updating client UI");
			textViewStatus.setText(videoStreamingReceiverClient.getStatus() +  " display fps: " + displayed * 1000
					/ (System.currentTimeMillis() - startTime) 
					+ " underflow (Hz) " + (System.currentTimeMillis() - startTime)/((underflow+1)*1000)
					+ " received " + displayed);
			Bitmap nextBitmap = videoStreamingReceiverClient.getNextBitmap();
			if(nextBitmap != null) {
				imageView.setImageBitmap(nextBitmap);
				displayed++;
			} else {
				underflow++;
			}
			lock.lock();
			try {
				if(!isPaused) {
					imageUpdaterHandler.postDelayed(imageUpdaterRunnable, 40);
				}
			} finally {
				lock.unlock();
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
		videoStreamingReceiverClient.mutateExit(false);
//		unbindService(mConnection);
		super.onDestroy();
		
		System.exit(0);
		//或者下面这种方式
		//android.os.Process.killProcess(android.os.Process.myPid()); 
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
            final View textEntryView = factory.inflate(R.layout.video3_client_alert_dialog_text_entry, null);
            //set saved value
            String[] settings =  getConf();
            String ip = settings[0];
            String port = settings[1];
        	final EditText ip_edit = (EditText)textEntryView.findViewById(R.id.ip_edit);
        	final EditText port_edit = (EditText)textEntryView.findViewById(R.id.port_edit);
            if(!"".equals(ip)){
            	ip_edit.setText(ip);
            	port_edit.setText(port);
            }
            
            new AlertDialog.Builder(ClientActivity.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.conf_dialog_title)
                .setView(textEntryView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	saveConf(ip_edit.getText().toString(),port_edit.getText().toString());
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
        String ip = settings.getString("ip", "");
        String port = settings.getString("port", "2013");
        return new String[]{ip,port};
	}
	private void saveConf(String ip,String port){
		Constants.serverIP = ip; 
		Constants.serverPort  = Integer.parseInt(port);
		final SharedPreferences settings = getSharedPreferences(Constants.settingTag, 0);
		SharedPreferences.Editor editor = settings.edit();
        editor.putString("ip", ip);
        editor.putString("port", port);
        editor.commit();
	}

}