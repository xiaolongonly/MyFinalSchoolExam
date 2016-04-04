package com.u1city.module.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author linjy
 * @time 2015-5-5 13:34:41
 * @类说明 网络工具类
 * 
 *      <pre>
 * 1.获取当前的网络状态 -1：没有网络 1：WIFI 0：MOBILE
 * 2.网络是否连接
 * 3.网络类型是否为wifi
 * </pre>
 */
public class NetUtil
{

    /**
     * 1.获取当前的网络状态 -1：没有网络 1：WIFI 0：MOBILE
     * 
     * @param context
     * @return
     */
    public static int getAPNType(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null)
        {
            return netType;
        }

        return networkInfo.getType();

    }

    // /**
    // *
    // * @return
    // *
    // */
    // public static boolean isNetworkConnected() {
    // Context context =App.getContext();
    // if (context != null) {
    // ConnectivityManager mConnectivityManager = (ConnectivityManager) context
    // .getSystemService(Context.CONNECTIVITY_SERVICE);
    // NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    // if (mNetworkInfo != null) {
    // return mNetworkInfo.isAvailable();
    // }
    // }
    // return false;
    // }
    /**
     * 2.网络是否连接
     * 
     * @param context
     * @return
     * 
     */
    public static boolean isNetworkConnected(Context context)
    {
        if (context != null)
        {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null)
            {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 3.网络类型是否为wifi
     * 
     * @param context
     * @return
     */
    public static boolean isWifi(Context context)
    {
        if (ConnectivityManager.TYPE_WIFI == getAPNType(context))
        {
            return true;
        }
        return false;
    }
}
