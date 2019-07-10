package com.example.androidtest2013.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.androidtest2013.R;

public class GalleryTestActivity extends AppCompatActivity {
    private int[] imagesIDs = new int[]{
            R.drawable.image1, R.drawable.image2, R.drawable.image3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_test);

        Gallery ga1 = findViewById(R.id.ga1);
        final ImageView iv1 = findViewById(R.id.ga_iv1);
         ga1.setAdapter(new ImageAdapter(GalleryTestActivity.this));
         ga1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                iv1.setImageResource(imagesIDs[i]);
             }
         });
    }

    public class ImageAdapter extends BaseAdapter{
        Context context;

        int itemBackground;

        public ImageAdapter(Context context){
            this.context = context;
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
        }

        @Override
        public int getCount() {
            return imagesIDs.length;
        }

        @Override
        public Object getItem(int i) {
            return imagesIDs[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;

            if(view == null){
                imageView = new ImageView(context);
                imageView.setImageResource(imagesIDs[i]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
            }else {
                imageView = (ImageView) view;
            }
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }
}
