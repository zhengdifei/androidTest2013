package com.example.androidtest2013.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.example.androidtest2013.MainActivity;
import com.example.androidtest2013.R;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

public class TextViewActivity extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);

        String html = "<font color='red'>Hello android</font><br/>";
        html += "<big><a href='http://www.baidu.com'>百度</a></big>";

        CharSequence charSequence = Html.fromHtml(html);
        tv1.setText(charSequence);
        tv1.setMovementMethod(LinkMovementMethod.getInstance());

        String text = "url: http://www.baidu.com\n";
        text += "email: plokmiu@sina.com\n" ;
        text += "phone: 13800000000";
        tv2.setText(text);

        TextView tv3 = findViewById(R.id.tv3);
        tv3.setTextColor(android.R.color.white);
        tv3.setBackgroundColor(android.R.color.black);
        tv3.setTextSize(20);

        //设定HTML标签样式，图片3为一个超链接标签a
        String html2 = "图像1<img src='image1'/> 图像2<img src='image2'/>\n";
        html2 += "图像3<a href='http://www.baidu.com'><img src='image3'/></a>";
        //fromHtml中ImageGetter选择html中<img>的图片资源
        CharSequence cs = Html.fromHtml(html2, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                Drawable daw = null;
                if(s.equals("image1")){
                    daw = getResources().getDrawable(R.drawable.image1);
                    daw.setBounds(0, 0, daw.getIntrinsicWidth()/3, daw.getIntrinsicHeight()/3);
                }else if(s.equals("image2")){
                    daw = getResources().getDrawable(R.drawable.image2);
                    daw.setBounds(0, 0, daw.getIntrinsicWidth()/2, daw.getIntrinsicHeight()/2);
                }else {
                    daw = getResources().getDrawable(getResouceId(s));
                    daw.setBounds(0, 0, daw.getIntrinsicWidth()/4, daw.getIntrinsicHeight()/4);
                }
                return daw;
            }
        }, null);
        tv3.setText(cs);
        tv3.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tv4 = findViewById(R.id.tv4);
        TextView tv5 = findViewById(R.id.tv5);
        String text1 = "显示activity1";
        String text2 = "显示activity2";
        //使用SpannableString存放字符串
        SpannableString spannableString1 = new SpannableString(text1);
        SpannableString spannableString2 = new SpannableString(text2);
        //通过setSpan设定文本块响应的点击事件
        //此处只设定文本的索引为2开始的文本块：Activity1
        spannableString1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TextViewActivity.this, UIsetActivity.class);
                startActivity(intent);
            }
        }, 2, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TextViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv4.setText(spannableString1);
        tv4.setMovementMethod(LinkMovementMethod.getInstance());

        tv5.setText(spannableString2);
        tv5.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tv6 = findViewById(R.id.tv6);
        String html6 = "之前讲解Android布局的时候，就已经说明，所有<a href='http://www.cnblogs.com/plokmju/p/androidUI_Layout.html'>Layout</a>都是View的子类或者间接子类。而TextView也一样，是View的直接子类。它是一个文本显示控件，提供了基本的显示文本的功能，并且是大部分UI控件的父类，因为大部分UI控件都需要展示信息。";
        CharSequence cs6 = Html.fromHtml(html6);
        tv6.setText(cs6);
        tv6.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public int getResouceId(String s){
        try {
            Field field = R.drawable.class.getField(s);
            return Integer.parseInt(field.get(null).toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
