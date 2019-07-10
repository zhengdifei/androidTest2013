package com.example.androidtest2013.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.androidtest2013.R;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_test);

        DatePicker dp1 = findViewById(R.id.dp1);
        TimePicker tp1 = findViewById(R.id.tp1);

        dp1.init(2013, 8, 20, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2);
                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                Log.e("zdf", format.format(calendar.getTime()));
            }
        });

        tp1.setIs24HourView(true);
        tp1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Log.e("zdf", "小时：" + i + "  分钟：" + i1);
            }
        });

        findViewById(R.id.dp_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog time = new TimePickerDialog(DatePickerTestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Log.e("zdf", "hour:"+ i + " minute:" + i1);
                    }
                }, 18, 25, true);

                time.show();
            }
        });

        findViewById(R.id.dp_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(DatePickerTestActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.e("zdf", "year：" + i + " month: " + i1 + " day:" + i2);
                    }
                }, 2013, 7, 20);

                datePickerDialog.show();
            }
        });
    }
}
