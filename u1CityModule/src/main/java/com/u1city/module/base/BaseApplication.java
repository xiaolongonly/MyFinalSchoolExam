package com.u1city.module.base;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.u1city.module.imageloader.AuthImageDownloader;
/**
 * 基础Application入口
 * 只是对ImageLoader进行了默认设置
 * @author zhengjb
 * */
public class BaseApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//设置ImageLoader的配置，可接受Https的图片
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .imageDownloader(new AuthImageDownloader(getApplicationContext(), 6*1000, 6 * 1000)) 
        .memoryCacheExtraOptions(480, 800)
        .diskCacheExtraOptions(480, 800, null)
        .build();
		ImageLoader.getInstance().init(config);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
