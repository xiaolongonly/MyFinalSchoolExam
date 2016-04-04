package com.u1city.module.util;

/**
 * 防止快速点击的工具
 * 
 * @author zhengjb
 * */
public class FastClickAvoider
{
    private  long lastClickTime;

    public synchronized boolean isFastClick()
    {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500)
        {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
