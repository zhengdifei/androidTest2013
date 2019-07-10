package com.example.androidtest2013.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Objects;

public class User implements Parcelable {
    private int id;
    private String username;
    private String password;

    protected User(Parcel in) {
        super();
    }

    public User(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return  Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            Log.e("User", "User 被反序列化了");
            return new User(in.readInt(), in.readString(), in.readString());
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.e("User", "User 被序列化了");
        parcel.writeInt(id);
        parcel.writeString(username);
        parcel.writeString(password);
    }
}
