package com.example.androidtest2013.app;

import java.io.Serializable;

public class MyCopyData implements Serializable {
    private String name;
    private int age;

    public MyCopyData(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MyCopyData{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
