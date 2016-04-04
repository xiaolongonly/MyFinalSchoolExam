package com.xiaolongonly.finalschoolexam.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.u1city.module.base.RequestJsonObject;
import com.u1city.module.common.HttpCallBack;
import com.u1city.module.common.StandardCallback;
import com.u1city.module.util.NetUtil;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ToastUtil;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by GuoXiaolong on 2016/3/16.
 */
public class MyRequest {
    /** 通过此tag可以拦截到所有请求路径日志 */
    private static final String TAG = MyRequest.class.getName();
    private RequestQueue queue;
    private Context context;

    /**
     * @author zhengjb
     * @date 2015-5-05
     * @time 下午5:29:29 类说明:
     *       远程数据端
     *       对远程数据的真正请求者
     * @param context 使用远程数据端的上下文
     * */
    public MyRequest(Context context)
    {
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    /**
     * 普通请求，参数都是加密的
     *
     * @param baseUrl 基础路径
     * @param jsonRequest 请求参数
     * @param callBack 回调，成功onSuccess，失败onError
     * */
    public void request(String baseUrl,  JSONObject jsonRequest, HttpCallBack callBack)
    {
        callBack.start();

        if (!NetUtil.isNetworkConnected(context))
        {
            ToastUtil.showNotNetToast(context);
            handlerCallbackError(callBack);
            return;
        }

        String content = getRequestContent(jsonRequest);
        RequestJsonObject requestJsonObject = new RequestJsonObject(Request.Method.GET, baseUrl + "?" + content, null, callBack);
        Log.i(TAG, baseUrl + "?" + content);
        queue.add(requestJsonObject);
    }
    /** 将errorResponse传递下去，同时如果callback是StandardCallback的时候
     * 要设置不对该errorResponse弹出提示信息
     * 因为前面已经默认弹出了信息
     * */
    private void handlerCallbackError(HttpCallBack callBack){
        if(callBack instanceof StandardCallback){
            boolean tempEnable = ((StandardCallback) callBack).isEnableToastError();
            ((StandardCallback) callBack).setEnableToastError(false);
            callBack.onErrorResponse(new VolleyError());
            ((StandardCallback) callBack).setEnableToastError(tempEnable);
        }else{
            callBack.onErrorResponse(new VolleyError());
        }
    }
    /**
     * 拼接请求参数
     * @param jsonRequest 请求参数集合
     * @return 请求参数获取
     */
    private String getRequestContent(JSONObject jsonRequest)
    {
        StringBuffer urlBuff = new StringBuffer();
        try
        {
            Iterator<String> iterator = jsonRequest.keys();
            while (iterator.hasNext())
            {
                String key = iterator.next();
                urlBuff.append("&" + key + "=" + StringUtils.utf8Encode(jsonRequest.getString(key)));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return urlBuff.toString().substring(1, urlBuff.toString().length());
    }
    /**
     *
     * 取消所有请求
     *
     * @param tag
     *
     */
    public void cancleAll(Object tag)
    {
        queue.cancelAll(tag);
    }
}
