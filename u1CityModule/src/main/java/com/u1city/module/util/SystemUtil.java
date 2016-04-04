/*
 * 系统: U1CityModule
 * 文件名: SystemUtil.java
 * 版权: U1CITY Corporation 2015
 * 描述: 
 * 创建人: zhengjb
 * 创建时间: 2015-6-27 下午3:19:27
 */
package com.u1city.module.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取系统相关信息的工具 如版本号、imei等在这里获取在这里面
 * 
 * @author zhengjb
 */
public class SystemUtil {
	/*
	 * 获取当前程序的版本号
	 */
	public String getVersionName(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	public String getAvailableStorage(Context context) {

		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			File sdCardFile = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath());
			return sdCardFile.getAbsolutePath();
		}

		String path = null;

		File sdCardFile = null;

		path = getPath("/storage/emulated");
		if(path == null){
			path = getPath("/mnt/sdcard");
		}
		
		if(path == null){
			path = getPath("/sdcard");
		}

		if (path != null) {
			sdCardFile = new File(path);
			return sdCardFile.getAbsolutePath();
		}

		return null;
	}

	public String getPath(String directory){
		String path = null;
		String[] devMountList = new File(directory).list();

		if (devMountList != null) {
			for (String devMount : devMountList) {
				File file = new File(directory + "/" + devMount);

				Log.d("SystemUtil", "address:" + devMount);

				if (file.isDirectory() && file.canWrite()) {
					path = file.getAbsolutePath();

					String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss")
							.format(new Date());
					File testWritable = new File(path, "test_" + timeStamp);

					if (testWritable.mkdirs()) {
						testWritable.delete();
					} else {
						path = null;
					}
				}
			}
		}
		
		return path;
	}
	
	/** 查看某个应用是否有安装
	 * @param context 调用接口的上下文
	 * @param packageName 包名
	 * @return true为已安装，false为未安装
	 *  */
	public final boolean isApkInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
