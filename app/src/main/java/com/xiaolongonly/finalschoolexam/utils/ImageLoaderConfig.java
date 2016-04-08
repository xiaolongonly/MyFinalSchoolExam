/*
 * 系统: U1CityDemo
 * 文件名: ImageLoaderConfig.java
 * 版权: U1CITY Corporation 2015
 * 描述: 
 * 创建人: zhengjb
 * 创建时间: 2015-9-25 下午3:17:23
 */
package com.xiaolongonly.finalschoolexam.utils;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.u1city.module.imageloader.AuthImageDownloader;

public class ImageLoaderConfig {
    public final static void setConfig(Context context) {
        // 初始化ImageLoader配置使其接受https的链接，框架中写在BaseApplication，一般项目的Application会继承BaseApplication
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).imageDownloader(new AuthImageDownloader(context, 6 * 1000, 6 * 1000)) // default
                .build();
        ImageLoader.getInstance().init(config);
    }
}
