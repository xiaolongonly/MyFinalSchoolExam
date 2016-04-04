package com.u1city.module.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * @author zhengjb
 * @time 2015-5-5 14:41:43
 * @类说明 Android的一些单位转化工具 例如px<-->dp转化 px<-->sp转化等等都可以写在这里
 * 
 *      <pre>
 * dp-->px
 * sp-->px
 * 获得屏幕宽度
 * 获得屏幕高度
 * </pre>
 * */
public class DimensUtil
{

    /**
     * dp-->px
     * 
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPixels(Context context, float dp)
    {
        Resources res = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param px
     * @return
     */ 
    public static int pixelsToDp(Context context, float px)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    
    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @return
     */ 
    public static int pixelsToSp(Context context, float px) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (px / fontScale + 0.5f); 
    } 

    /**
     * sp-->px
     * 
     * @param context
     * @param sp
     * @return
     */
    public static int spToPixels(Context context, float sp)
    {
        Resources res = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 获得屏幕宽度
     * 
     * @param context
     * @return
     */
    public static int getDisplayWidth(Context context)
    {
        return ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 获得屏幕高度
     * 
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context)
    {
        return ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
    }
}
