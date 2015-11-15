package com.songjian.youfaner.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

/**
 * 屏幕截图工具类
 * 
 * @author Administrator
 * 
 */
public class ScreenshotUtil {
	/**
	 * 重新创建位图
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap tekeScreenShot(Activity activity) {
		// 获取需要截图的view
		View view = activity.getWindow().getDecorView();
		// View view = activity.getWindow().getCurrentFocus();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b = view.getDrawingCache();
		// 获取状态栏高度
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		// 获取屏幕宽高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
		Bitmap bitmap = Bitmap.createBitmap(b, 0, statusBarHeight, width,
				height - statusBarHeight);
		view.destroyDrawingCache();
		return bitmap;
	}

	/**
	 * 保存位图到指定路径
	 * 
	 * @param bitmap
	 * @param filePath
	 */
	public static void savePic(Bitmap bitmap, File filePath) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			if (null != fos) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 屏幕截图并保存到指定路径
	 * 
	 * @param activity
	 * @param filePath
	 */
	public static void screenshot(Activity activity, File filePath) {
		if (filePath == null) {
			System.out.println("保存失败！");
			return;
		} else if (filePath.getParentFile().exists()) {
			filePath.getParentFile().mkdirs();
		}
		ScreenshotUtil.savePic(ScreenshotUtil.tekeScreenShot(activity),
				filePath);
	}
}
