package com.songjian.youfaner.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * ͼƬ��������
 */
public class BitmapUtil {
	/**
	 * λͼת��Ϊ�ֽ�����
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * ��ȡͼƬ��ת�Ƕ�
	 * 
	 * @param filePath
	 * @return
	 */
	public static int getExifOrientation(String filePath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	/**
	 * �ֽ�����ת��Ϊbitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap Bytes2Bitmap(byte[] b) {
		if (b != null && b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	/**
	 * λͼ������
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap
				.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newBitmap;
	}

	/**
	 * ��ȡ�������������ֽ�����
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] readStream(InputStream is) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = is.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		is.close();
		return outputStream.toByteArray();
	}

	/**
	 * �������ȡͼƬ�������ֽ�����
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static byte[] getBitmapBytes(String url) throws Exception {
		URL pathUrl = new URL(url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) pathUrl
				.openConnection();
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.setReadTimeout(5000);
		InputStream is = null;
		byte[] bytes = new byte[1024];
		int len = -1;
		if (httpURLConnection.getResponseCode() == 200) {
			is = httpURLConnection.getInputStream();
			byte[] result = readStream(is);
			is.close();
			return result;
		}
		return null;
	}

	/**
	 * �������ȡͼƬ
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getBitmap(String url) throws Exception {
		byte[] data = null;
		data = getBitmapBytes(url);
		int length = data.length;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, length);
		return bitmap;
	}
}
