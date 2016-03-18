package com.xiaolongonly.finalschoolexam.utils;

import android.content.Context;

import com.u1city.module.base.RemoteClient;
import com.u1city.module.common.HttpCallBack;

import org.json.JSONObject;

/**
 * @author zhengjb
 *         请求接口
 *         2015-05-15
 */
public class RequestApi {
    private static RequestApi requestApi = null;

    //  public static final String SERVER_URL = "http://easysupport.wx.jaeapp.com/easySupport";// 正式
//    public static final String SERVER_URL = "http://easysupport-1.wx.jaeapp.com/easySupport";// 测试

    private static RemoteClient remoteClient;

    public static RequestApi getInstance(Context context) {
        if (requestApi == null) {
            requestApi = new RequestApi();
        }
        remoteClient = new RemoteClient(context);
        return requestApi;
    }

    /**
     * 取消所有请求
     */
    public void cancleAll(Object tag) {
        remoteClient.cancleAll(tag);
    }

//    public void getActiveInfomationList(int pageIndex, int pageSize, HttpCallBack callBack) {
//        JSONObject jsonRequest = new JSONObject();
//        try {
//            jsonRequest.put("BusinessId", "14529");
//            jsonRequest.put("CustomerId", 2253);
//            jsonRequest.put("PageIndex", pageIndex);
//            jsonRequest.put("PageSize", pageSize);
//            jsonRequest.put("PageType", 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        remoteClient.request(SERVER_URL, "EasySupport.GetActiveInfomationList", jsonRequest, callBack);
//    }
//
//    /**
//     * 获取商品类型
//     */
//    public void getCustomerProTypeList(HttpCallBack callBack) {
//        JSONObject jsonRequest = new JSONObject();
//        try {
//            jsonRequest.put("CustomerId", 2253);
//            jsonRequest.put("BusinessId", "14529");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        remoteClient.request(SERVER_URL, "EasySupport.GetCustomerProTypeList", jsonRequest, callBack);
//    }
}
