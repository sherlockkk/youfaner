package com.songjian.youfaner;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.songjian.youfaner.utils.BitmapUtil;

public class MainActivity extends Activity implements OnClickListener {
	private static final int CAPTURE_CHOOSE = 1;
	private static final int PICTURE_CHOOSE = 2;
	private Button button1;
	private Button button2;
	private Button button3;
	private ImageView imageView;

	private Bitmap imageBitmap;

	private String age;
	private String gender = "";
	private String range;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initWidget();
	}

	private void initWidget() {
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		imageView = (ImageView) findViewById(R.id.imageView);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 拍照
		case R.id.button1:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(Environment.getExternalStorageDirectory(),
					"temp.jpg");
			Uri uri = Uri.fromFile(file);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, CAPTURE_CHOOSE);
			break;
		// 分析
		case R.id.button2:
			Toast.makeText(this, "+++++++++", Toast.LENGTH_SHORT).show();
			DetectFace detectFace = new DetectFace();
			detectFace.detect(imageBitmap);
			detectFace.setDetectCallback(new DetectCallback() {

				@Override
				public void detectResult(JSONObject result) {
					// TODO Auto-generated method stub
					// 设置红色的线条框
					Paint paint = new Paint();
					paint.setColor(Color.RED);
					paint.setStrokeWidth(Math.max(imageBitmap.getWidth(),
							imageBitmap.getHeight()) / 100f);
					// 对位图的预处理
					final Bitmap bitmap = Bitmap.createBitmap(
							imageBitmap.getWidth(), imageBitmap.getHeight(),
							imageBitmap.getConfig());
					// 将处理后的位图放置到画布中
					Canvas canvas = new Canvas(bitmap);
					canvas.drawBitmap(imageBitmap, new Matrix(), null);
					// 搜索图中所有的脸
					try {
						int count = result.getJSONArray("face").length();
						for (int i = 0; i < count; i++) {
							float x, y, w, h;
							// 获取中心点坐标
							x = (float) result.getJSONArray("face")
									.getJSONObject(i).getJSONObject("position")
									.getJSONObject("center").getDouble("x");
							y = (float) result.getJSONArray("face")
									.getJSONObject(i).getJSONObject("position")
									.getJSONObject("center").getDouble("y");
							// 获取脸的尺寸大小
							w = (float) result.getJSONArray("face")
									.getJSONObject(i).getJSONObject("position")
									.getDouble("width");
							h = (float) result.getJSONArray("face")
									.getJSONObject(i).getJSONObject("position")
									.getDouble("height");

							age = result.getJSONArray("face").getJSONObject(i)
									.getJSONObject("attribute")
									.getJSONObject("age").getString("value");
							range = result.getJSONArray("face")
									.getJSONObject(i)
									.getJSONObject("attribute")
									.getJSONObject("age").getString("range");
							gender = result.getJSONArray("face")
									.getJSONObject(i)
									.getJSONObject("attribute")
									.getJSONObject("gender").getString("value");

							// 重新以百分比计算图片参数
							x = x / 100 * imageBitmap.getWidth();
							w = w / 100 * imageBitmap.getWidth() * 0.7f;
							y = y / 100 * imageBitmap.getHeight();
							h = h / 100 * imageBitmap.getHeight() * 0.7f;
							// 绘制人脸框
							canvas.drawLine(x - w, y - h, x - w, y + h, paint);
							canvas.drawLine(x - w, y - h, x + w, y - h, paint);
							canvas.drawLine(x + w, y + h, x - w, y + h, paint);
							canvas.drawLine(x + w, y + h, x + w, y - h, paint);

							System.out.println(x + " " + y + " " + w + " " + h
									+ " ");
						}
					} catch (JSONException e) {
						e.printStackTrace();
						MainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(MainActivity.this,
										"JSONException!", Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
					System.out.println(result);
				}

			});
			break;
		// 图库
		case R.id.button3:
			Intent intent2 = new Intent(Intent.ACTION_PICK);
			intent2.setType("image/*");
			startActivityForResult(intent2, PICTURE_CHOOSE);
			break;

		default:
			break;
		}
	}

	private class DetectFace {
		DetectCallback callback = null;

		public void setDetectCallback(DetectCallback detectCallback) {
			callback = detectCallback;
		}

		public void detect(final Bitmap image) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 向服务器发起请求，打开输出流
					HttpRequests httpRequests = new HttpRequests(
							"1a477e1c8f45f6a233dc509bac154f17",
							"vkG34ggOrwMjSnsQXq2zxK-2T2HUe0QH", true, false);
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					// 将位图进行压缩
					float scale = Math.min(600f / (float) image.getWidth(),
							(float) 600f / image.getHeight());
					Matrix matrix = new Matrix();
					matrix.postScale(scale, scale);

					Bitmap imageSmall = Bitmap.createBitmap(image, 0, 0,
							image.getWidth(), image.getHeight(), matrix, false);

					// 将压缩后的位图写入指定的outputstream
					imageSmall.compress(Bitmap.CompressFormat.JPEG, 100,
							byteArrayOutputStream);
					byte[] byteArray = byteArrayOutputStream.toByteArray();
					try {
						// 发送参数，服务器进行参数分析
						JSONObject result = httpRequests
								.detectionDetect(new PostParameters()
										.setAttribute(
												"age,gender,race,smiling,glass,pose")
										.setImg(byteArray));
						// 完成分析，执行回调
						if (callback != null) {
							callback.detectResult(result);
						}

					} catch (FaceppParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(MainActivity.this,
										"网络都没有，识别个**！", Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				}
			}).start();
		}
	}

	// 返回json
	private interface DetectCallback {
		void detectResult(JSONObject result);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAPTURE_CHOOSE) {
				File file = new File(Environment.getExternalStorageDirectory(),
						"temp.jpg");
				String filePath = file.getAbsolutePath();
				Options options = new Options();
				options.inJustDecodeBounds = true;
				imageBitmap = BitmapFactory.decodeFile(filePath, options);
				// 测量图片大小
				options.inSampleSize = (int) Math.max(1, Math.ceil(Math.max(
						(double) options.outWidth / 1024f,
						(double) options.outHeight / 1024f)));
				options.inJustDecodeBounds = false;
				imageBitmap = BitmapFactory.decodeFile(filePath, options);
				Matrix matrix = new Matrix();
				matrix.postRotate(BitmapUtil.getExifOrientation(file.getPath()));
				int width = imageBitmap.getWidth();
				int height = imageBitmap.getHeight();
				imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, width,
						height, matrix, true);
				imageView.setImageBitmap(imageBitmap);
				button2.setEnabled(true);
			} else {
				if (requestCode == PICTURE_CHOOSE) {
					if (data != null) {
						Cursor cursor = getContentResolver().query(
								data.getData(), null, null, null, null);
						cursor.moveToFirst();
						int index = cursor.getColumnIndex(ImageColumns.DATA);
						String fileSrc = cursor.getString(index);

						Options options = new Options();
						options.inJustDecodeBounds = true;
						imageBitmap = BitmapFactory
								.decodeFile(fileSrc, options);

						options.inSampleSize = Math.max(1, (int) Math.ceil(Math
								.max((double) options.outWidth / 1024f,
										(double) options.outHeight / 1024f)));
						options.inJustDecodeBounds = false;

						imageBitmap = BitmapFactory
								.decodeFile(fileSrc, options);
						Matrix matrix = new Matrix();
						matrix.postRotate(BitmapUtil
								.getExifOrientation(fileSrc));
						int width = imageBitmap.getWidth();
						int height = imageBitmap.getHeight();
						imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
								width, height, matrix, true);
						imageView.setImageBitmap(imageBitmap);
						button2.setEnabled(true);
					}
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
