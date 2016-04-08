package com.xiaolongonly.finalschoolexam.api;

import android.content.Context;

import com.u1city.module.common.HttpCallBack;
import com.xiaolongonly.finalschoolexam.utils.MyRequest;

import org.json.JSONObject;

/**
 * Created by GuoXiaolong on 2016/3/30.
 */
public class RequestApi {
    private static final String TAG = "RequestApi";
    private static RequestApi requestApi = null;
    private static final String SERVER_URL="http://120.27.45.196/xiaolong/index.php/sql/";

    private static MyRequest myRequest;
    public static RequestApi getInstance(Context context)
    {
        if(requestApi ==null)
        {
            requestApi =new RequestApi();
        }
        myRequest = new MyRequest(context);
        return requestApi;
    }
    /**
     * 取消所有请求
     */
    public void cancleAll(Object tag) {
        myRequest.cancleAll(tag);
    }

    public void execSQL(String sql, HttpCallBack callBack) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("sql", sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        myRequest.request(SERVER_URL, jsonRequest, callBack);
    }
}
