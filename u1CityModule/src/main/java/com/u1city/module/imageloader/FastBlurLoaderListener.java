/*
 * 系统: U1CityModule
 * 文件名: FastBlurLoaderListener.java
 * 版权: U1CITY Corporation 2015
 * 描述: 
 * 创建人: zhengjb
 * 创建时间: 2015-8-17 下午5:40:45
 */
package com.u1city.module.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.u1city.module.util.ImageUtils;

/**
 * 用于将获取的图片加上毛玻璃效果
 * 
 * @author zhengjb
 */
public class FastBlurLoaderListener implements ImageLoadingListener
{
    private final Activity activity;
    private final ImageView imageView;
    
    private int radius = 20;
    
    private Drawable cancelDrawable;
    
    private Drawable failedDrawable;
    
    /**
     * 用于将获取的图片加上毛玻璃效果
     * 默认的模糊程度为20
     * 
     * @param context
     * @param imageView 要设置图片的控件
     * 
     * @author zhengjb
     */
    public FastBlurLoaderListener(Activity activity, ImageView imageView){
        this(activity, imageView, 20);
    }
    
    /**
     * 用于将获取的图片加上毛玻璃效果
     * 
     * @param context
     * @param imageView 要设置图片的控件
     * @param radius 毛玻璃的模糊程度
     * 
     * @author zhengjb
     */
    public FastBlurLoaderListener(Activity activity, ImageView imageView, int radius){
        this.activity = activity;
        this.imageView = imageView;
        this.radius = radius;
    }
    
    /**
     * 用于将获取的图片加上毛玻璃效果
     * 
     * @param context
     * @param imageView 要设置图片的控件
     * @param radius 毛玻璃的模糊程度
     * @param defaultDrawableId 默认图片id
     * 
     * @author zhengjb
     */
    public FastBlurLoaderListener(Activity activity, ImageView imageView, int radius, int defaultDrawableId){
        this(activity, imageView, radius);
        setCancelDrawable(defaultDrawableId);
        setFailedDrawable(defaultDrawableId);
    }
    
    /**
     * 用于将获取的图片加上毛玻璃效果
     * 
     * @param context
     * @param imageView 要设置图片的控件
     * @param radius 毛玻璃的模糊程度
     * @param defaultDrawable 默认图片
     * 
     * @author zhengjb
     */
    public FastBlurLoaderListener(Activity activity, ImageView imageView, int radius, Drawable defaultDrawable){
        this(activity, imageView, radius);
        setCancelDrawable(defaultDrawable);
        setFailedDrawable(defaultDrawable);
    }
    
    /** 设置图片加载取消时的背景图片 */
    public void setCancelDrawable(Drawable cancelDrawable)
    {
        this.cancelDrawable = cancelDrawable;
    }

    /** 设置图片加载失败时的背景图片 */
    public void setFailedDrawable(Drawable failedDrawable)
    {
        this.failedDrawable = failedDrawable;
    }
    
    /** 通过资源文件id设置图片加载取消时的背景图片 */
    public void setCancelDrawable(int id)
    {
        this.cancelDrawable = activity.getResources().getDrawable(id);
    }

    /** 通过资源文件id设置图片加载失败时的背景图片 */
    public void setFailedDrawable(int id)
    {
        this.failedDrawable = activity.getResources().getDrawable(id);
    }
    
    @Override
    public void onLoadingCancelled(String arg0, View arg1)
    {
        if(cancelDrawable != null){
            imageView.setBackground(cancelDrawable);
        }
    }

    @Override
    public void onLoadingComplete(String arg0, View arg1, final Bitmap bitmap)
    {
        new Thread(){
            @Override
            public void run()
            {
                super.run();
                
                final Bitmap fastblur = ImageUtils.fastblur(activity, bitmap, radius);
                
                activity.runOnUiThread(new Runnable()
                {
                    
                    @Override
                    public void run()
                    {
                        imageView.setImageBitmap(fastblur);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
    {
        if(failedDrawable != null){
            imageView.setBackground(failedDrawable);
        }
    }

    @Override
    public void onLoadingStarted(String arg0, View arg1)
    {
    }

}
