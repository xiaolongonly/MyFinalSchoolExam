package com.u1city.module.base;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.u1city.module.common.Debug;
import com.u1city.module.common.HttpCallBack;
import com.u1city.module.common.StandardCallback;
import com.u1city.module.util.AESHelper;
import com.u1city.module.util.NetUtil;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhengjb
 * @date 2015-5-05
 * @time 下午5:29:29 类说明:
 *       远程数据端
 *       对远程数据的真正请求者
 * */
public class RemoteClient
{
    /** 通过此tag可以拦截到所有请求路径日志 */
    private static final String TAG = RemoteClient.class.getName();
    private static final String CODE_STRING = "u1city";
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
    public RemoteClient(Context context)
    {
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    /**
     * 普通请求，参数都是加密的
     * 
     * @param baseUrl 基础路径
     * @param method 访问方法
     * @param jsonRequest 请求参数
     * @param callBack 回调，成功onSuccess，失败onError
     * */
    public void request(String baseUrl, String method, JSONObject jsonRequest, HttpCallBack callBack)
    {
        callBack.start();

        if (!NetUtil.isNetworkConnected(context))
        {
            ToastUtil.showNotNetToast(context);
            handlerCallbackError(callBack);
            return;
        }

        try
        {
            String date = StringUtils.getSimpleDate(System.currentTimeMillis());
            // 一些默认的设置，与服务端的约定参数
            jsonRequest.put("postTime", StringUtils.utf8Encode(date));// 时间戳参数
            StringBuffer buff = getRequestToken(jsonRequest).append(method).append(AESHelper.AESEncrypt(CODE_STRING));
            jsonRequest.put("method", StringUtils.utf8Encode(method));
            jsonRequest.put("format", StringUtils.utf8Encode("json"));
            jsonRequest.put("user", StringUtils.utf8Encode(CODE_STRING));
            jsonRequest.put("token", StringUtils.utf8Encode(getToken(buff)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String content = getRequestContent(jsonRequest);
        RequestJsonObject requestJsonObject = new RequestJsonObject(Request.Method.GET, baseUrl + "?" + content, null, callBack);
        Debug.i(TAG, baseUrl + "?" + content);

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
     * 当参数中有不需要加密的参数时使用此方法
     * 要在那些不需要加密的参数之前先完成加密动作，即调用encryptToken
     * 
     * @param baseUrl 基础路径
     * @param method 访问方法
     * @param jsonRequest 请求参数
     * @param callBack 回调，成功onSuccess，失败onError
     * */
    public void requestAfterEncrypt(String baseUrl, String method, JSONObject jsonRequest, HttpCallBack callBack)
    {
        callBack.start();

        if (!NetUtil.isNetworkConnected(context))
        {
            ToastUtil.showNotNetToast(context);
            handlerCallbackError(callBack);
            return;
        }

        try
        {
            jsonRequest.put("method", StringUtils.utf8Encode(method));
            jsonRequest.put("format", StringUtils.utf8Encode("json"));
            jsonRequest.put("user", StringUtils.utf8Encode(CODE_STRING));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String content = getRequestContent(jsonRequest);
        RequestJsonObject requestJsonObject = new RequestJsonObject(Request.Method.GET, baseUrl + "?" + content, null, callBack);
        Debug.i(TAG, baseUrl + "?" + content);

        queue.add(requestJsonObject);
    }

    /**
     * 普通post请求，参数都是加密的
     * 
     * @param baseUrl 基础路径
     * @param method 访问方法
     * @param jsonRequest 请求参数
     * @param callBack 回调，成功onSuccess，失败onError
     * */
    public void post(String baseUrl, String method, Map<String, String> jsonRequest, HttpCallBack callBack)
    {
        callBack.start();

        if (!NetUtil.isNetworkConnected(context))
        {
            ToastUtil.showNotNetToast(context);
            handlerCallbackError(callBack);
            return;
        }

        try
        {
            String date = StringUtils.getSimpleDate(System.currentTimeMillis());
            // 一些默认的设置，与服务端的约定参数
            jsonRequest.put("postTime", StringUtils.utf8Encode(date));// 时间戳参数
            StringBuffer buff = getRequetMap(jsonRequest).append(method).append(AESHelper.AESEncrypt(CODE_STRING));
            jsonRequest.put("method", StringUtils.utf8Encode(method));
            jsonRequest.put("format", StringUtils.utf8Encode("json"));
            jsonRequest.put("user", StringUtils.utf8Encode(CODE_STRING));
            jsonRequest.put("token", StringUtils.utf8Encode(getToken(buff)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        PostRequestJsonObject requestJsonObject = new PostRequestJsonObject(baseUrl, jsonRequest, callBack);
        queue.add(requestJsonObject);
        Debug.i(TAG, requestJsonObject.getUrl() + " --method:" + method + " -- params:" + jsonRequest);
    }

    /**
     * 当参数中有不需要加密的参数时使用此方法
     * 要在那些不需要加密的参数之前先完成加密动作，即调用encryptToken
     * 
     * @param baseUrl 基础路径
     * @param method 访问方法
     * @param jsonRequest 请求参数
     * @param callBack 回调，成功onSuccess，失败onError
     * */
    public void postAfterEncrypt(String baseUrl, String method, Map<String, String> jsonRequest, HttpCallBack callBack)
    {
        callBack.start();

        if (!NetUtil.isNetworkConnected(context))
        {
            ToastUtil.showNotNetToast(context);
            handlerCallbackError(callBack);
            return;
        }

        try
        {
            jsonRequest.put("method", StringUtils.utf8Encode(method));
            jsonRequest.put("format", StringUtils.utf8Encode("json"));
            jsonRequest.put("user", StringUtils.utf8Encode(CODE_STRING));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        PostRequestJsonObject requestJsonObject = new PostRequestJsonObject(baseUrl, jsonRequest, callBack);
        queue.add(requestJsonObject);
        Debug.i(TAG, requestJsonObject.getUrl() + " --method:" + method + " -- params:" + jsonRequest);
    }

    /**
     * 将请求集合拼接起来
     * */
    private StringBuffer getRequetMap(Map<String, String> params)
    {
        StringBuffer buff = new StringBuffer();
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            buff.append(key);
            buff.append(params.get(key));
        }
        return buff;
    }

    /**
     * 在不需要加密的参数之前调用
     * 
     * @param jsonRequest 需要加密的参数
     * @param method 请求的方法
     * */
    public void encryptToken(JSONObject jsonRequest, String method) throws JSONException
    {
        String date = StringUtils.getSimpleDate(System.currentTimeMillis());
        jsonRequest.put("postTime", StringUtils.utf8Encode(date));// 时间戳参数
        StringBuffer buff = getRequestToken(jsonRequest).append(method).append(AESHelper.AESEncrypt(CODE_STRING));
        jsonRequest.put("token", StringUtils.utf8Encode(getToken(buff)));
    }

    /**
     * 在不需要加密的参数之前调用
     * 
     * @param jsonRequest 需要加密的参数
     * @param method 请求的方法
     * */
    public void encryptToken(Map<String, String> jsonRequest, String method) throws JSONException
    {
        String date = StringUtils.getSimpleDate(System.currentTimeMillis());
        jsonRequest.put("postTime", StringUtils.utf8Encode(date));// 时间戳参数
        StringBuffer buff = getRequetMap(jsonRequest).append(method).append(AESHelper.AESEncrypt(CODE_STRING));
        jsonRequest.put("token", StringUtils.utf8Encode(getToken(buff)));
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

    /**
     * 字符串排序
     */
    String Asc(String input)
    {
        char[] arr = input.toCharArray();
        Arrays.sort(arr);
        String strAsc = "";
        for (char item : arr)
            strAsc += item;
        return strAsc;
    }

    /**
     * 
     * @param content 进行加密的文字
     * @return 获取认证
     * 
     */
    private String getToken(StringBuffer content)
    {
        try
        {
            String strAsc = Asc(content.toString().trim().replace(" ", "").toLowerCase());
            return encryptMD5(strAsc);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密
     * @param val 要加密的文字
     * @return 加密后的文字
     */
    public static String encryptMD5(String val) throws NoSuchAlgorithmException
    {
        StringBuffer sb = new StringBuffer(32);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArray;
        try
        {
            byteArray = val.getBytes("gb2312");
            byte[] m = md5.digest(byteArray);
            for (int i = 0; i < m.length; i++)
            {
                int ch = m[i];
                sb.append(org.apache.commons.lang.StringUtils.leftPad(Integer.toHexString(ch & 0xff), 2, '0'));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 对一些参数进行加密
     * @param jsonRequest 要加密的请求参数的集合
     * @return 获取请求需要的加密参数
     */
    private StringBuffer getRequestToken(JSONObject jsonRequest)
    {
        StringBuffer buff = new StringBuffer();
        try
        {
            Iterator<String> iterator = jsonRequest.keys();
            while (iterator.hasNext())
            {
                String key = iterator.next();
                buff.append(key);
                buff.append(jsonRequest.getString(key));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return buff;

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

}
