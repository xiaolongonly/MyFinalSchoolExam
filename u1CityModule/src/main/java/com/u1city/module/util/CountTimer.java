package com.u1city.module.util;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.u1city.module.R;

/**
 * 倒数计时工具类，可用在发送校验码的时候
 * */
public class CountTimer extends CountDownTimer
{
    public static final int TIME_COUNT = 60000;// 时间防止从119s开始显示（以倒计时120s为例子）
    private TextView btn;
    private int endStrRid;
    private int normalColor, timingColor;// 未计时的文字颜色，计时期间的文字颜色
    private Drawable background;
    private OnBacllkCountTimer bacllkCountTimer;
    private boolean isBackgroundColor = true;//是否要默认的背景

    /**
     * 参数 millisInFuture 倒计时总时间（如60S，120s等）
     * 参数 countDownInterval 渐变时间（每次倒计1s）
     * 参数 btn 点击的按钮(因为Button是TextView子类，为了通用我的参数设置为TextView）
     * 参数 endStrRid 倒计时结束后，按钮对应显示的文字
     */
    public CountTimer(long millisInFuture, long countDownInterval, TextView btn, int endStrRid)
    {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.endStrRid = endStrRid;
        this.background = btn.getBackground();
    }

    /**
     * 参数上面有注释
     */
    public CountTimer(TextView btn, int endStrRid)
    {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = endStrRid;
        this.background = btn.getBackground();
    }

    public CountTimer(TextView btn)
    {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = R.string.txt_getMsgCode_validate;
        this.background = btn.getBackground();
    }

    public CountTimer(TextView tv_varify, int normalColor, int timingColor)
    {
        this(tv_varify);
        this.normalColor = normalColor;
        this.timingColor = timingColor;
    }

    // 计时完毕时触发
    @Override
    public void onFinish()
    {
        if(bacllkCountTimer != null)
        {
            bacllkCountTimer.endTimerTvColor();
        }
        if (normalColor > 0)
        {
            btn.setTextColor(normalColor);
        }
        btn.setText(endStrRid);
        btn.setEnabled(true);
        btn.setBackgroundDrawable(background);
    }

    // 计时过程显示
    @Override
    public void onTick(long millisUntilFinished)
    {
        if(bacllkCountTimer != null)
        {
            bacllkCountTimer.startTimerTvColor();
        }
        if (timingColor > 0)
        {
            btn.setTextColor(timingColor);
        }
        btn.setEnabled(false);
        if(isBackgroundColor)
        {
            btn.setBackgroundResource(R.drawable.bg_counttimer);
        }
        btn.setText(millisUntilFinished / 1000 + "秒后重发");
    }
    
    public void setBackgroundColor(boolean isBackgroundColor)
    {
        this.isBackgroundColor = isBackgroundColor;
    }
    
    public void setBacllkCountTimer(OnBacllkCountTimer bacllkCountTimer)
    {
        this.bacllkCountTimer = bacllkCountTimer;
    }
    
    //设置颜色回调
    public interface OnBacllkCountTimer
    {
        void startTimerTvColor();
        void endTimerTvColor();
    }
}
