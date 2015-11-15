package com.songjian.youfaner.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ���繤����
 * 
 * @author Administrator
 * 
 */
public class NetworkUtil {
	/**
	 * ��ȡ��ǰ������Ϣ���ж��Ƿ����
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
