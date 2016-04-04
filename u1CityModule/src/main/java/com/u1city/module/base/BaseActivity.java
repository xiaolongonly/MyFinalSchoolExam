package com.u1city.module.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.u1city.module.R;
import com.u1city.module.common.Debug;

import java.util.Date;

/***************************************
 * 
 * @author lwli
 * @date 2014-8-15
 * @modify zhengjb
 * @date 2015-3-25
 * @time 上午9:21:35 类说明: 基础Activity类 对广播、加载进度显示、overridePendingTransition、网络状态变化等
 *       进行了处理
 * 
 * 
 **************************************/
public abstract class BaseActivity extends FragmentActivity
{
    public static final String TAG = BaseActivity.class.getName();

    private ProgressBar loadingBar;

    /** 使用者定义并使用的广播 */
    private BroadcastReceiver broadcastReceiver;
    /** 网络状态转变的广播 */
    private BroadcastReceiver connectionChangeReceiver;
    /** 使用者定义的广播意图过滤器 */
    private IntentFilter intentFilter = new IntentFilter();

    private boolean isFirstLoading = true;
    private boolean connetionChangeEnable = true;
    /** 标志网络是否连接着 */
    private boolean isNetConnected = true;

    private long onCreateTime;

    /**
     * 
     * @param savedInstanceState
     * @param layoutResId
     * @param layoutActionBar
     * @return
     * 
     */
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState, int layoutResId, int layoutActionBar)
    {
        onCreateTime = new Date().getTime();
        // Debug.d(BaseActivity.TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if (layoutActionBar != 0)
        {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        }

        setContentView(layoutResId);

        if (layoutActionBar != 0)
        {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutActionBar);
            loadingBar = (ProgressBar) findViewById(R.id.pb_loading);
        }

        init();
    }

    /** @return 是否第一次加载数据，如果是则会在title上显示圆形进度视图，加载完后设置该数据为false */
    public boolean isFirstLoading()
    {
        return isFirstLoading;
    }

    /** @param isFirstLoading 是否是第一次进入页面加载数据，如果是显示loadingBar，否则不显示 */
    public void setFirstLoading(boolean isFirstLoading)
    {
        this.isFirstLoading = isFirstLoading;
    }

    /**
     * @return 是否在加载中
     * @author zhengjb
     */
    public boolean isLoading()
    {
        return loadingBar.getVisibility() == View.VISIBLE;
    }

    /**
     * 默认不开启注册广播，设置广播的过滤同时开启广播
     * 
     * @param intentFilter 此activity中的广播过滤器
     */
    public void setIntentFilter(IntentFilter intentFilter)
    {
        // Debug.d("TAG", "setIntentFilter:" + intentFilter.getAction(0));
        this.intentFilter = intentFilter;
        registerBroadCast();
    }

    /** 在activity被销毁时，注销广播 */
    private void unRegisterBroadCast()
    {
        if (broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        if (connectionChangeReceiver != null)
        {
            unregisterReceiver(connectionChangeReceiver);
            connectionChangeReceiver = null;
        }
    }

    /** 为activity准备的广播接收器，主要为了实现activity在需要刷新的时候刷新，也可以有其他用途 */
    public void registerBroadCast()
    {
        if (broadcastReceiver == null)
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    onReceiveBroadCast(context, intent);
                }
            };

            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    /**
     * 当接受到广播后的处理
     * 
     * @param context 接受广播的上下文
     * @param intent 该广播的意图
     * */
    public void onReceiveBroadCast(Context context, Intent intent)
    {

    }

    /**
     * 拥有默认动作的启动activity方法
     * 
     * @param intent 要启动activity的意图
     * @param isFinish 是否关闭当前activity
     * */
    public void startActivity(Intent intent, boolean isFinish)
    {
        super.startActivity(intent);

        if (isFinish)
        {
            finish();
        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 拥有默认动作的启动activity方法
     * 
     * @param intent 要启动activity的意图
     * @param reqCode 请求码
     * @param isFinish 是否关闭当前activity
     * */
    public void startActivityForResult(Intent intent, int reqCode, boolean isFinish)
    {
        super.startActivityForResult(intent, reqCode);

        if (isFinish)
        {
            finish();
        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Debug.d(TAG, "create view used time:" + (new Date().getTime() - onCreateTime) + "ms");
        onCreateTime = new Date().getTime();
    }

    /** 运行在onCreate中，实现视图和数据的初始化 */
    public void init()
    {
        initView();
        initData();
        registerConnectionChange();
    }

    /** @return 是否注册网络状态变化的广播，如果否请在initData之前或之中设置 */
    public boolean isConnetionChangeEnable()
    {
        return connetionChangeEnable;
    }

    /** @param 是否注册网络状态变化的广播，如果否请在initData之前或之中设置 */
    public void setConnetionChangeEnable(boolean connetionChangeEnable)
    {
        this.connetionChangeEnable = connetionChangeEnable;
    }

    /** 注册网络状态变更的广播 */
    private void registerConnectionChange()
    {
        Debug.i(TAG, "registerConnectionChange");
        if (connetionChangeEnable && connectionChangeReceiver == null)
        {
            connectionChangeReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    Debug.i(TAG, "on connection change");
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo mobileNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                    // wifi、移动网络都没有连接
                    if (!wifiNetInfo.isConnected() && !mobileNetInfo.isConnected())
                    {
                        isNetConnected = false;
                        onNetBreakUp();
                    }
                    else
                    {
                        if (!isNetConnected)
                        {// 重新连接的状态
                            isNetConnected = true;
                            onNetReConnected();
                        }
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionChangeReceiver, intentFilter);
        }
    }

    /** 当网络中断时的处理 */
    public void onNetBreakUp()
    {
        Debug.i(TAG, "net break up");
    }

    /** 当网络重新连接上时的处理 */
    public void onNetReConnected()
    {
        Debug.i(TAG, "net re-connected");
    }

    /** 显示title的加载视图 */
    public void startLoading()
    {
        if (loadingBar != null && loadingBar.getVisibility() == View.GONE)
        {
            loadingBar.setVisibility(View.VISIBLE);
        }
    }

    /** 隐藏title的加载视图 */
    public void stopLoading()
    {
        if (loadingBar != null && loadingBar.getVisibility() == View.VISIBLE)
        {
            loadingBar.setVisibility(View.GONE);
        }
    }

    /** 使用默认动作关闭 */
    public void finishAnimation()
    {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /** 初始化视图 */
    public void initView()
    {
    };

    /** 加载数据，初次加载显示title的loading */
    public void initData()
    {
        if (isFirstLoading)
        {
            startLoading();
            isFirstLoading = false;
        }
    };

    @Override
    public void onBackPressed()
    {
        finishAnimation();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unRegisterBroadCast();
    }
}
