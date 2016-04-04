package com.u1city.module.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.u1city.module.R;
/**
 * 简化toast的调用
 * @author zhengjb
 * */
public class ToastUtil extends Toast
{
    public static final String TOAST_ERROR = "服务器开了会小差，稍后再试吧~";
    public static final String TOAST_NO_NET = "亲，您的网络不太给力哟，稍后再试吧~";
    
    public ToastUtil(Context context)
    {
        super(context);
    }

    /**
     * toast 提示消息
     * 
     * @param context
     * @param msg
     * @return
     * @return
     */
    public static void showToast(Context context, String msg)
    {
        final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                toast.cancel();
            }
        }, 500);

    }

    /**
     * 
     * @param msg
     * 
     */
    public static void showToastLong(Context context, String msg)
    {
        final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
    }

    /**
     * toast 提示消息
     * 
     * @param context
     * @param msg
     * @return
     * @return
     */
    public static void showNotNetToast(Context context)
    {
        final Toast toast = Toast.makeText(context, TOAST_NO_NET, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(100);
        toast.setText(TOAST_NO_NET);
        toast.show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                toast.cancel();
            }
        }, 700);
    }

    /**
     * toast 提示消息
     * 
     * @param context
     * @param msg
     * @return
     * @return
     */
    public static void showErrorToast(Context context)
    {
        final Toast toast = Toast.makeText(context, TOAST_ERROR, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(100);
        toast.setText(TOAST_ERROR);
        toast.show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                toast.cancel();
            }
        }, 700);
    }

    public static void showCtrlSuccessful(Context context)
    {
        Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
    }

    public static void showCtrlFailed(Context context)
    {
        Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
    }

    /**
     * 
     * @param msg
     *            商品管理提示类，自定义Toast
     */
    public static void showNotNetToast(Context context, String msg)
    {
        final ToastUtil toast = new ToastUtil(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(context);
        tv.setMinWidth(dm.widthPixels);
        params.leftMargin = 5;
        params.rightMargin = 5;
        tv.setMinHeight(dm.heightPixels / 23);
        tv.setText(msg);
        tv.setBackgroundResource(R.drawable.bg_toast);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.getBackground().setAlpha(220);
        toast.setDuration(100);
        toast.setView(tv);
        toast.setGravity(Gravity.TOP, 0, (int) (dm.density * 85 + 10));
        toast.show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                toast.cancel();
            }
        }, 700);
    }

    /**
     * 
     * @param msg
     *            商品管理提示类，自定义Toast
     */
    public static void showNewDayToast(Context context, String msg)
    {
        ToastUtil toast = new ToastUtil(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(context);
        tv.setMinWidth(dm.widthPixels);
        params.leftMargin = 5;
        params.rightMargin = 5;
        tv.setMinHeight(dm.heightPixels / 23);
        tv.setText(msg);
        tv.setBackgroundResource(R.drawable.bg_toast);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.getBackground().setAlpha(220);
        toast.setDuration(100);
        toast.setView(tv);
        toast.setGravity(Gravity.TOP, 0, (int) (dm.density * 85 + 10));
        toast.show();
    }

    /**
     * 
     * @param msg
     *            商品管理提示类，自定义Toast
     */
    public static void showMainToast(Context context, String msg, int height)
    {
        ToastUtil toast = new ToastUtil(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(context);
        tv.setMinWidth(dm.widthPixels);
        params.leftMargin = 5;
        params.rightMargin = 5;
        tv.setMinHeight(dm.heightPixels / 23);
        tv.setText(msg);
        tv.setBackgroundResource(R.drawable.bg_toast);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.getBackground().setAlpha(220);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tv);
        toast.setGravity(Gravity.BOTTOM, dm.widthPixels / 4 + (dm.widthPixels / 4) / 2, height);
        toast.show();
    }

}
