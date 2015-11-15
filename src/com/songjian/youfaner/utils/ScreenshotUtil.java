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
 * ��Ļ��ͼ������
 * 
 * @author Administrator
 * 
 */
public class ScreenshotUtil {
	/**
	 * ���´���λͼ
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap tekeScreenShot(Activity activity) {
		// ��ȡ��Ҫ��ͼ��view
		View view = activity.getWindow().getDecorView();
		// View view = activity.getWindow().getCurrentFocus();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b = view.getDrawingCache();
		// ��ȡ״̬���߶�
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		// ��ȡ��Ļ���
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// ȥ��������
		Bitmap bitmap = Bitmap.createBitmap(b, 0, statusBarHeight, width,
				height - statusBarHeight);
		view.destroyDrawingCache();
		return bitmap;
	}

	/**
	 * ����λͼ��ָ��·��
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
	 * ��Ļ��ͼ�����浽ָ��·��
	 * 
	 * @param activity
	 * @param filePath
	 */
	public static void screenshot(Activity activity, File filePath) {
		if (filePath == null) {
			System.out.println("����ʧ�ܣ�");
			return;
		} else if (filePath.getParentFile().exists()) {
			filePath.getParentFile().mkdirs();
		}
		ScreenshotUtil.savePic(ScreenshotUtil.tekeScreenShot(activity),
				filePath);
	}
}
