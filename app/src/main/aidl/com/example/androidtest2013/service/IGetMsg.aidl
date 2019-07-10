// Message.aidl
package com.example.androidtest2013.service;

// 这是两个自定义类
import com.example.androidtest2013.service.Message;
import com.example.androidtest2013.service.User;

interface IGetMsg{
    // 在AIDL接口中定义一个getMes方法
    List<Message> getMes(in User u);
}
