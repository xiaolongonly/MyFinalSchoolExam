package com.xiaolongonly.finalschoolexam.model;

/**
 * Created by GuoXiaolong on 2016/3/17.
 */
public class ChatModel {
    private int state;//消息状态
    private String message;//消息内容
    private String receive_time;//接收时间
    private String fromuser_id;//来自哪个用户
    private String touser_id;//发给谁
    private String userName;
    private String userImage;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public String getFromuser_id() {
        return fromuser_id;
    }

    public void setFromuser_id(String fromuser_id) {
        this.fromuser_id = fromuser_id;
    }

    public String getTouser_id() {
        return touser_id;
    }

    public void setTouser_id(String touser_id) {
        this.touser_id = touser_id;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatModel{" +
                "state=" + state +
                ", message='" + message + '\'' +
                ", receive_time='" + receive_time + '\'' +
                ", fromuser_id='" + fromuser_id + '\'' +
                ", touser_id='" + touser_id + '\'' +
                ", userName='" + userName + '\'' +
                ", userImage='" + userImage + '\'' +
                '}';
    }
}
