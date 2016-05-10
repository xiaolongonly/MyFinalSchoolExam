package com.xiaolongonly.finalschoolexam.model;

import java.io.Serializable;

/**
 * Created by GuoXiaolong on 2016/3/30.
 */
public class TaskModel implements Comparable<TaskModel>,Serializable {
    public static final int STATU_UNTAKE=1;//未接取
    public static final int STATU_ALL=5;//全部
    public static final int STATU_HAVETAKE=2; //已接取
    public static final int STATU_HAVEFINISH=3;// 已完成
    public static final int STATU_HAVECLOSE=4;//已关闭
    public static final int STATU_WAITCONFIRM=0;
    private int task_id;
    private int publisher_id;
    private int picker_id;
    private String task_title;
    private String task_content;
    private String task_locationx;//纬度
    private String task_locationy;//经度

    private String task_finishtime;
    private String task_statu;
    private String task_location;
    private String task_createtime;
    private String total;

    public String getTask_createtime() {
        return task_createtime;
    }

    public void setTask_createtime(String task_createtime) {
        this.task_createtime = task_createtime;
    }

    public String getTask_location() {
        return task_location;
    }

    public void setTask_location(String task_location) {
        this.task_location = task_location;
    }

    public int getPicker_id() {
        return picker_id;
    }

    public void setPicker_id(int picker_id) {
        this.picker_id = picker_id;
    }

    public int getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(int publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getTask_content() {
        return task_content;
    }

    public void setTask_content(String task_content) {
        this.task_content = task_content;
    }


    public String getTask_finishtime() {
        return task_finishtime;
    }

    public void setTask_finishtime(String task_finishtime) {
        this.task_finishtime = task_finishtime;
    }


    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTask_locationx() {
        return task_locationx;
    }

    public void setTask_locationx(String task_locationx) {
        this.task_locationx = task_locationx;
    }

    public String getTask_locationy() {
        return task_locationy;
    }


    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setTask_locationy(String task_locationy) {
        this.task_locationy = task_locationy;
    }

    public String getTask_statu() {
        return task_statu;
    }

    public void setTask_statu(String task_status) {
        this.task_statu = task_status;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    @Override
    public String toString() {
        return "TaskModel{" +
                "picker_id=" + picker_id +
                ", task_id=" + task_id +
                ", publisher_id=" + publisher_id +
                ", task_title='" + task_title + '\'' +
                ", task_content='" + task_content + '\'' +
                ", task_locationx='" + task_locationx + '\'' +
                ", task_locationy='" + task_locationy + '\'' +
                ", task_finishtime='" + task_finishtime + '\'' +
                ", task_statu='" + task_statu + '\'' +
                ", task_location='" + task_location + '\'' +
                ", task_createtime='" + task_createtime + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
    @Override
    public int compareTo(TaskModel another) {
        int anotherStatu = Integer.valueOf(another.task_statu);
        int thisStatu = Integer.valueOf(task_statu);
        if(anotherStatu!=thisStatu)
        {
            return thisStatu-anotherStatu;
        }
        else
        {
            return Integer.valueOf(another.task_id)-Integer.valueOf(task_id);
        }

    }
}
