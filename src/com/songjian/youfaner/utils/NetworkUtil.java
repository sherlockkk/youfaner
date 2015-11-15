package com.songjian.youfaner.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * 
 * @author Administrator
 * 
 */
public class NetworkUtil {
	/**
	 * 获取当前网络信息，判断是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnectioned(Context context) {
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}

}
