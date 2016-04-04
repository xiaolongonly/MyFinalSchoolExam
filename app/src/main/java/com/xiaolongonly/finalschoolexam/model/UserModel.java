package com.xiaolongonly.finalschoolexam.model;

import java.io.Serializable;

/**
 * Created by GuoXiaolong on 2016/3/30.
 */
public class UserModel implements Serializable {
    private int user_id;
    private String user_account;
    private String user_password;
    private String user_tel;
    private String user_qq;
    private String user_name;

    @Override
    public String toString() {
        return "UserModel{" +
                "user_id=" + user_id +
                ", user_account='" + user_account + '\'' +
                ", user_password='" + user_password + '\'' +
                ", user_tel='" + user_tel + '\'' +
                ", user_qq='" + user_qq + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_imageurl='" + user_imageurl + '\'' +
                '}';
    }

    public String getUser_imageurl() {
        return user_imageurl;
    }

    public void setUser_imageurl(String user_imageurl) {
        this.user_imageurl = user_imageurl;
    }

    private String user_imageurl;
    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }



    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(String user_qq) {
        this.user_qq = user_qq;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }
}
