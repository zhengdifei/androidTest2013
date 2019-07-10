package com.example.androidtest2013.app;

import android.app.Application;

public class MyApp extends Application {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setName("zdf");
    }
}
