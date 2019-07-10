/**
 * Name        : ImageUtilEngine.java
 * Copyright   : Copyright (c) Tencent Inc. All rights reserved.
 * Description : TODO
 */

package com.example.androidtest2013.video3.spore.jni;

//import android.graphics.Bitmap;

/**
 * @author ianmao
 */
public class ImageUtilEngine {

    static {
        System.loadLibrary("JNITest");
    }

    public native int[] decodeYUV420SP(byte[] buf, int width, int heigth);
}
