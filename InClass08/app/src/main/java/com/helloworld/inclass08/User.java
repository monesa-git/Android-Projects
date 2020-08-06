package com.helloworld.inclass08;

import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable {
    String fname, lname, email;
    int id;
    String tokenKey;

    @Override
    public String toString() {
        return "User{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", tokenKey='" + tokenKey + '\'' +
                '}';
    }
}
