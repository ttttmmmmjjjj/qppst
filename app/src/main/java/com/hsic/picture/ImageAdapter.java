package com.hsic.picture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hsic.tmj.qppst.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
	private Activity uiActivity;
	private ArrayList<String> paths;
	private int size;
	private ImageShowManager imageManager;
	private LayoutInflater li;

	public ImageAdapter(Activity a, ArrayList<String> paths) {
		this.uiActivity = a;
		this.paths = paths;
		size = paths.size();
		imageManager = ImageShowManager.from(uiActivity);
		li = LayoutInflater.from(uiActivity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return paths.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		SurfaceHolder surfaceHolder = new SurfaceHolder();
		if (convertView != null) {
			surfaceHolder = (SurfaceHolder) convertView.getTag();
		} else {
			convertView = li.inflate(R.layout.image_item, null);
			surfaceHolder.iv = (ImageView) convertView
					.findViewById(R.id.imageView1);

		}
		convertView.setTag(surfaceHolder);

		String path = paths.get(position);
		if (cancelPotentialLoad(path, surfaceHolder.iv)) {
			AsyncLoadImageTask task = new AsyncLoadImageTask(surfaceHolder.iv);
			surfaceHolder.iv.setImageDrawable(new LoadingDrawable(task));
			task.execute(path);
		}
		return convertView;
	}

	static class SurfaceHolder {
		ImageView iv;
	}

	/**
	 * �жϵ�ǰ��imageview�Ƿ��ڼ�����ͬ����Դ
	 * 
	 * @param url
	 * @param imageview
	 * @return
	 */
	private boolean cancelPotentialLoad(String url, ImageView imageview) {

		AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
		if (loadImageTask != null) {
			String bitmapUrl = loadImageTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				loadImageTask.cancel(true);
			} else {
				// ��ͬ��url�Ѿ��ڼ�����.
				return false;
			}
		}
		return true;
	}

	/**
	 * �������ͼƬ���첽�߳�
	 * 
	 * @author Administrator
	 * 
	 */
	class AsyncLoadImageTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> imageViewReference;
		private String url = null;

		public AsyncLoadImageTask(ImageView imageview) {
			super();
			imageViewReference = new WeakReference<ImageView>(imageview);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			/**
			 * ����Ļ�ȡbitmap�Ĳ��֣����̣� ���ڴ滺������ȡ�����û����Ӳ�̻�������ȡ�����û�д�sd��/�����ȡ
			 */

			Bitmap bitmap = null;
			this.url = params[0];

			// ���ڴ滺�������ȡ
			bitmap = imageManager.getBitmapFromMemory(url);
			if (bitmap != null) {
				Log.d("dqq", "return by �ڴ�");
				return bitmap;
			}
			// ��Ӳ�̻��������ж�ȡ
			bitmap = imageManager.getBitmapFormDisk(url);
			if (bitmap != null) {
				imageManager.putBitmapToMemery(url, bitmap);
				Log.d("dqq", "return by Ӳ��");
				return bitmap;
			}

			// û�л������ԭʼλ�ö�ȡ
			bitmap = BitmapUtilities.getBitmapThumbnail(url,
					ImageShowManager.bitmap_width,
					ImageShowManager.bitmap_height);
			imageManager.putBitmapToMemery(url, bitmap);
			imageManager.putBitmapToDisk(url, bitmap);
			Log.d("dqq", "return by ԭʼ��ȡ");
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap resultBitmap) {
			if (isCancelled()) {
				// ��ȡ����
				resultBitmap = null;
			}
			if (imageViewReference != null) {
				ImageView imageview = imageViewReference.get();
				AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
				if (this == loadImageTask) {
					imageview.setImageDrawable(null);
					imageview.setImageBitmap(resultBitmap);
				}
			}

			super.onPostExecute(resultBitmap);
		}
	}

	/**
	 * ����imageview���������Ϊ��imageview�첽�������ݵĺ���
	 * 
	 * @param imageview
	 * @return
	 */
	private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview) {
		if (imageview != null) {
			Drawable drawable = imageview.getDrawable();
			if (drawable instanceof LoadingDrawable) {
				LoadingDrawable loadedDrawable = (LoadingDrawable) drawable;
				return loadedDrawable.getLoadImageTask();
			}
		}
		return null;
	}

	/**
	 * ��¼imageview��Ӧ�ļ������񣬲�������Ĭ�ϵ�drawable
	 * 
	 * @author Administrator
	 * 
	 */
	public static class LoadingDrawable extends ColorDrawable {
		// ������drawable������ĵļ����߳�
		private final WeakReference<AsyncLoadImageTask> loadImageTaskReference;

		public LoadingDrawable(AsyncLoadImageTask loadImageTask) {
			super(Color.LTGRAY);
			loadImageTaskReference = new WeakReference<AsyncLoadImageTask>(
					loadImageTask);
		}

		public AsyncLoadImageTask getLoadImageTask() {
			return loadImageTaskReference.get();
		}
	}

}
