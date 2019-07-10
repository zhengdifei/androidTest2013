package com.example.androidtest2013.event;

import android.app.Activity;
import android.util.Log;
import android.view.View;

public class SendSmsListener implements View.OnLongClickListener {
    private Activity act;

    public SendSmsListener(Activity act){
        this.act = act;
    }

    @Override
    public boolean onLongClick(View view) {
        Log.e("zdf", "on long click");
        return false;
    }
}
