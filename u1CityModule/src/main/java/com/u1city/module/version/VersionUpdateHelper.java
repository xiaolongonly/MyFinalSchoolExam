package com.u1city.module.version;

import android.util.Log;

//版本检查帮组类
public class VersionUpdateHelper {

	/*
	 * 检查版本是否需要升级，true表示需要升级，false表示无需升级
	 */
	public static boolean checkVersion(String currentVersion,
			String serviceVersion) {	
		
		// 拆分版本信息
		String versionNumList[] = (null != currentVersion
				&& currentVersion.length() > 0 ? currentVersion.split("[.]") : null);
		String serviceVersionNumList[] = (null != serviceVersion
				&& serviceVersion.length() > 0 ? serviceVersion.split("[.]") : null);

		boolean result = false;
		if (null != versionNumList && versionNumList.length > 2
				&& null != serviceVersionNumList
				&& serviceVersionNumList.length > 2) {
			// 当前版本
			int version_1 = Integer.parseInt(versionNumList[0]);// 当前版本的主版本，如1.0.24中的1
			int version_2 = Integer.parseInt(versionNumList[1]);// 当前版本的次版本，如1.0.24中的0
			int version_3 = Integer.parseInt(versionNumList[2]);// 当前版本的次版本，如1.0.24中的24

			int serviceVersion_1 = Integer.parseInt(serviceVersionNumList[0]);// 服务端版本的主版本，如1.0.24中的1
			int serviceVersion_2 = Integer.parseInt(serviceVersionNumList[1]);// 服务端版本的次版本，如1.0.24中的0
			int serviceVersion_3 = Integer.parseInt(serviceVersionNumList[2]);// 服务端版本的次版本，如1.0.24中的24

			if (version_1 < serviceVersion_1) {
				// 当前主版本小于服务端主版本

				Log.i("升级版本比对", "版本version_1比服务端小, 需升级");
				result = true;
			} else if (version_1 == serviceVersion_1) {
				// 当前主版本等于服务端主版本

				if (version_2 < serviceVersion_2) {
					Log.i("升级版本比对", "版本version_2比服务端小, 需升级");

					result = true;
				} else if (version_2 == serviceVersion_2
						&& version_3 < serviceVersion_3) {
					Log.i("升级版本比对", "版本version_3比服务端小, 需升级");

					result = true;
				}
			}
		}

		return result;
	}
}
