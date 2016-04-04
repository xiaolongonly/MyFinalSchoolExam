package com.u1city.module.common;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.base.BaseFragment;

import org.json.JSONObject;

/***************************************
 * 
 * @author lwli
 * @date 2014-9-18
 * @time 下午5:29:50 类说明:网络请求接口回调
 * 
 **************************************/

/**
 * 建议使用StandardCallback，StandardCallback已经实现了一些标准的处理
 * 
 * */
@Deprecated
public abstract class HttpCallBack implements Listener<JSONObject>, ErrorListener
{
    protected Dialog dialog;
    protected Activity context;
    protected Fragment fragment;

    public HttpCallBack(Dialog dialog)
    {
        this.dialog = dialog;
    }

    public HttpCallBack(Activity context)
    {
        this.context = context;
    }

    public HttpCallBack(Fragment fragment)
    {
        this.fragment = fragment;
    }

    /**
     * 
     * 请求失败
     */
    @Override
    public void onErrorResponse(VolleyError arg0)
    {
        finish();
        onFailure(arg0);
    }

    /**
     * 
     * 请求成功
     */
    @Override
    public void onResponse(JSONObject response)
    {
        finish();
        onSuccess(response);
    }

    /**
     * 
     * 请求成功
     */
    public abstract void onSuccess(JSONObject response);

    /**
     * 
     * 请求失败
     */
    public abstract void onFailure(VolleyError error);

    /**
     * 网络请求开始
     * 
     */
    public void start()
    {
        if (dialog != null && !dialog.isShowing())
        {
            dialog.show();
        }
    }
    
    /** 回调结束 */
    public void finish(){
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
        else if (fragment != null && fragment instanceof BaseFragment)
        {
            ((BaseFragment)fragment).stopLoading();
        }
        else
        {
            if (context != null && context instanceof BaseActivity)
            {
                ((BaseActivity)context).stopLoading();
            }
        }
    }
}
