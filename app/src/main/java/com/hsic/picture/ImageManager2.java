package com.hsic.picture;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * ͼƬ������
 * 
 * @author �쿡��
 */
public class ImageManager2 {

	private static ImageManager2 imageManager;
	public LruCache<String, Bitmap> mMemoryCache;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	public DiskLruCache mDiskCache;

	private static Application myapp;

	/** ͼƬ���ض��У�����ȳ� */
	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();

	/** ͼƬ������У��Ƚ��ȳ������ڴ���ѷ��͵����� */
	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();

	/** ͼƬ�����߳���Ϣ������ */
	private Handler mImageLoaderHandler;

	/** ͼƬ�����߳��Ƿ���� */
	private boolean mImageLoaderIdle = true;

	/** ����ͼƬ */
	private static final int MSG_REQUEST = 1;
	/** ͼƬ������� */
	private static final int MSG_REPLY = 2;
	/** ��ֹͼƬ�����߳� */
	private static final int MSG_STOP = 3;
	/** ���ͼƬ�Ǵ�������أ���Ӧ�ý��Զ���������ӻ��������Ӧ�ö��� */
	private boolean isFromNet = true;

	/**
	 * ��ȡ������ֻ����UI�߳���ʹ�á�
	 * 
	 * @param context
	 * @return
	 */
	public static ImageManager2 from(Context context) {

		// �������ui�߳��У����׳��쳣
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}

		if (myapp == null) {
			myapp = (Application) context.getApplicationContext();
		}

		if (imageManager == null) {
			imageManager = new ImageManager2(myapp);
		}

		return imageManager;
	}

	/**
	 * ˽�й��캯������֤����ģʽ
	 * 
	 * @param context
	 */
	private ImageManager2(Context context) {
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		memClass = memClass > 32 ? 32 : memClass;
		// ʹ�ÿ����ڴ��1/8��ΪͼƬ����
		final int cacheSize = 1024 * 1024 * memClass / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

		};

		File cacheDir = DiskLruCache
				.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
		mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);

	}

	/**
	 * ���ͼƬ��Ϣ
	 */
	class ImageRef {

		/** ͼƬ��ӦImageView�ؼ� */
		ImageView imageView;
		/** ͼƬURL��ַ */
		String url;
		/** ͼƬ����·�� */
		String filePath;
		/** Ĭ��ͼ��ԴID */
		int resId;
		int width = 0;
		int height = 0;

		/**
		 * ���캯��
		 * 
		 * @param imageView
		 * @param url
		 * @param resId
		 * @param filePath
		 */
		ImageRef(ImageView imageView, String url, String filePath, int resId) {
			this.imageView = imageView;
			this.url = url;
			this.filePath = filePath;
			this.resId = resId;
		}

		ImageRef(ImageView imageView, String url, String filePath, int resId,
				int width, int height) {
			this.imageView = imageView;
			this.url = url;
			this.filePath = filePath;
			this.resId = resId;
			this.width = width;
			this.height = height;
		}

	}

	/**
	 * ��ʾͼƬ
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 */
	public void displayImage(ImageView imageView, String url, int resId) {

		if (imageView == null) {
			return;
		}
		if (imageView.getTag() != null
				&& imageView.getTag().toString().equals(url)) {
			return;
		}
		if (resId >= 0) {
			if (imageView.getBackground() == null) {
				imageView.setBackgroundResource(resId);
			}
			imageView.setImageDrawable(null);
		}
		if (url == null || url.equals("")) {
			return;
		}

		// ���url tag
		imageView.setTag(url);

		// ��ȡmap����
		Bitmap bitmap = mMemoryCache.get(url);
		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
			return;
		}

		// �����ļ���
		String filePath = urlToFilePath(url);
		if (filePath == null) {
			return;
		}

		queueImage(new ImageRef(imageView, url, filePath, resId));
	}

	/**
	 * ��ʾͼƬ�̶���СͼƬ������ͼ��һ��������ʾ�б��ͼƬ�����Դ���С�ڴ�ʹ��
	 * 
	 * @param imageView
	 *            ����ͼƬ�Ŀؼ�
	 * @param url
	 *            ���ص�ַ
	 * @param resId
	 *            Ĭ��ͼƬ
	 * @param width
	 *            ָ�����
	 * @param height
	 *            ָ���߶�
	 */
	public void displayImage(ImageView imageView, String url, int resId,
			int width, int height) {
		if (imageView == null) {
			return;
		}
		if (resId >= 0) {

			if (imageView.getBackground() == null) {
				imageView.setBackgroundResource(resId);
			}
			imageView.setImageDrawable(null);

		}
		if (url == null || url.equals("")) {
			return;
		}

		// ���url tag
		imageView.setTag(url);
		// ��ȡmap����
		Bitmap bitmap = mMemoryCache.get(url + width + height);
		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
			return;
		}

		// �����ļ���
		String filePath = urlToFilePath(url);
		if (filePath == null) {
			return;
		}

		queueImage(new ImageRef(imageView, url, filePath, resId, width, height));
	}

	/**
	 * ��ӣ�����ȳ�
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {

		// ɾ������ImageView
		Iterator<ImageRef> iterator = mImageQueue.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().imageView == imageRef.imageView) {
				iterator.remove();
			}
		}

		// �������
		mImageQueue.push(imageRef);
		sendRequest();
	}

	/**
	 * ��������
	 */
	private void sendRequest() {

		// ����ͼƬ�����߳�
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(
					imageLoader.getLooper());
		}

		// ��������
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST,
					imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}

	/**
	 * ͼƬ�����߳�
	 */
	class ImageLoaderHandler extends Handler {

		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg == null)
				return;

			switch (msg.what) {

			case MSG_REQUEST: // �յ�����
				Bitmap bitmap = null;
				Bitmap tBitmap = null;
				if (msg.obj != null && msg.obj instanceof ImageRef) {

					ImageRef imageRef = (ImageRef) msg.obj;
					String url = imageRef.url;
					if (url == null)
						return;
					// �������url����ȡsd���ͼƬ����ֱ�Ӷ�ȡ�����þ���DiskCache
					if (url.toLowerCase().contains("dcim")) {

						tBitmap = null;
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inSampleSize = 1;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(url, opt);
						int bitmapSize = opt.outHeight * opt.outWidth * 4;
						opt.inSampleSize = bitmapSize / (1000 * 2000);
						opt.inJustDecodeBounds = false;
						tBitmap = BitmapFactory.decodeFile(url, opt);
						if (imageRef.width != 0 && imageRef.height != 0) {
							bitmap = ThumbnailUtils.extractThumbnail(tBitmap,
									imageRef.width, imageRef.height,
									ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
							isFromNet = true;
						} else {
							bitmap = tBitmap;
							tBitmap = null;
						}

					} else
						bitmap = mDiskCache.get(url);

					if (bitmap != null) {
						// ToolUtil.log("��disk�����ȡ");
						// д��map����
						if (imageRef.width != 0 && imageRef.height != 0) {
							if (mMemoryCache.get(url + imageRef.width
									+ imageRef.height) == null)
								mMemoryCache.put(url + imageRef.width
										+ imageRef.height, bitmap);
						} else {
							if (mMemoryCache.get(url) == null)
								mMemoryCache.put(url, bitmap);
						}

					} else {
						try {
							byte[] data = loadByteArrayFromNetwork(url);

							if (data != null) {

								BitmapFactory.Options opt = new BitmapFactory.Options();
								opt.inSampleSize = 1;

								opt.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(data, 0,
										data.length, opt);
								int bitmapSize = opt.outHeight * opt.outWidth
										* 4;// pixels*3 if it's RGB and pixels*4
											// if it's ARGB
								if (bitmapSize > 1000 * 1200)
									opt.inSampleSize = 2;
								opt.inJustDecodeBounds = false;
								tBitmap = BitmapFactory.decodeByteArray(data,
										0, data.length, opt);
								if (imageRef.width != 0 && imageRef.height != 0) {
									bitmap = ThumbnailUtils
											.extractThumbnail(
													tBitmap,
													imageRef.width,
													imageRef.height,
													ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
								} else {
									bitmap = tBitmap;
									tBitmap = null;
								}

								if (bitmap != null && url != null) {
									// д��SD��
									if (imageRef.width != 0
											&& imageRef.height != 0) {
										mDiskCache.put(url + imageRef.width
												+ imageRef.height, bitmap);
										mMemoryCache.put(url + imageRef.width
												+ imageRef.height, bitmap);
									} else {
										mDiskCache.put(url, bitmap);
										mMemoryCache.put(url, bitmap);
									}
									isFromNet = true;
								}
							}
						} catch (OutOfMemoryError e) {
						}

					}

				}

				if (mImageManagerHandler != null) {
					Message message = mImageManagerHandler.obtainMessage(
							MSG_REPLY, bitmap);
					mImageManagerHandler.sendMessage(message);
				}
				break;

			case MSG_STOP: // �յ���ָֹ��
				Looper.myLooper().quit();
				break;

			}
		}
	}

	/** UI�߳���Ϣ������ */
	private Handler mImageManagerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {

				case MSG_REPLY: // �յ�Ӧ��

					do {
						ImageRef imageRef = mRequestQueue.remove();

						if (imageRef == null)
							break;

						if (imageRef.imageView == null
								|| imageRef.imageView.getTag() == null
								|| imageRef.url == null)
							break;

						if (!(msg.obj instanceof Bitmap) || msg.obj == null) {
							break;
						}
						Bitmap bitmap = (Bitmap) msg.obj;

						// ��ͬһImageView
						if (!(imageRef.url).equals(imageRef.imageView.getTag())) {
							break;
						}

						setImageBitmap(imageRef.imageView, bitmap, isFromNet);
						isFromNet = false;

					} while (false);

					break;
				}
			}
			// �������ñ�־
			mImageLoaderIdle = true;

			// ������δ�رգ�������һ������
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};

	/**
	 * ���ͼƬ��ʾ���ֶ���
	 * 
	 */
	private void setImageBitmap(ImageView imageView, Bitmap bitmap,
			boolean isTran) {
		if (isTran) {
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(bitmap) });
			td.setCrossFadeEnabled(true);
			imageView.setImageDrawable(td);
			td.startTransition(300);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * �������ȡͼƬ�ֽ�����
	 * 
	 * @param url
	 * @return
	 */
	private byte[] loadByteArrayFromNetwork(String url) {

		// try {
		//
		// HttpGet method = new HttpGet(url);
		// HttpResponse response = myapp.getHttpClient().execute(method);
		// HttpEntity entity = response.getEntity();
		// return EntityUtils.toByteArray(entity);
		//
		// } catch (Exception e) {
		// return null;
		// }

		return null;

	}

	/**
	 * ����url���ɻ����ļ�����·����
	 * 
	 * @param url
	 * @return
	 */
	public String urlToFilePath(String url) {

		// ��չ��λ��
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}

		StringBuilder filePath = new StringBuilder();

		// ͼƬ��ȡ·��
		filePath.append(myapp.getCacheDir().toString()).append('/');

		// ͼƬ�ļ���
		filePath.append(MD5.Md5(url)).append(url.substring(index));

		return filePath.toString();
	}

	/**
	 * Activity#onStop��ListView�����в�������
	 */
	public void stop() {

		// ����������
		mImageQueue.clear();

	}

}
