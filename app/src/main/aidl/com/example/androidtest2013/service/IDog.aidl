// IDog.aidl
package com.example.androidtest2013.service;

// Declare any non-default types here with import statements

interface IDog {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String getName();
    int getAge();
    void setAge(int age);
}
