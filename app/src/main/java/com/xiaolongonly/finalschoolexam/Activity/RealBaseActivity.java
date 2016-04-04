package com.xiaolongonly.finalschoolexam.activity;

import android.os.Bundle;

import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.Debug;
import com.xiaolongonly.finalschoolexam.utils.RequestApi;

/**
 * @author guoxl
 * 
 *         2016/03/15
 * 
 *         继承BaseActivity，并实现一些业务.
 * */
public class RealBaseActivity extends BaseActivity
{
    @Override
    protected void onPause()
    {
        Debug.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestApi.getInstance(this).cancleAll(this);
    }

    @Override
    protected void onResume()
    {
        Debug.d(TAG, "onResume");
        
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
