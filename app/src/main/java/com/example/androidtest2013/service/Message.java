package com.example.androidtest2013.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Message implements Parcelable {
    private int id;
    private String msgText;
    private String fromName;
    private String date;

    protected Message(Parcel in) {
        super();
    }

    public Message(int id, String msgText, String fromName, String date){
        this.id = id;
        this.fromName = fromName;
        this.msgText = msgText;
        this.date = date;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            Log.e("Message", "Message 被反序列化");
            return new Message(in.readInt(), in.readString(), in.readString(), in.readString());
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.e("Message", "Message 被序列化");
        parcel.writeInt(id);
        parcel.writeString(msgText);
        parcel.writeString(fromName);
        parcel.writeString(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", msgText='" + msgText + '\'' +
                ", fromName='" + fromName + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
