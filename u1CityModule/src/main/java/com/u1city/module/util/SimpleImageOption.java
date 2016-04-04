package com.u1city.module.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 简单获取imageLoader的图片设置
 * 
 * @author zhengjb
 * */
public class SimpleImageOption
{
    public static DisplayImageOptions create(int drawable)
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(drawable).showImageForEmptyUri(drawable).showImageOnFail(drawable).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).considerExifParams(true).build();

        return options;
    }
}
