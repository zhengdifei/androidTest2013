package com.example.androidtest2013.event;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;

public class MyButton extends android.support.v7.widget.AppCompatButton {
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        Log.e("zdf", "on key down");
        return true;
    }
}
