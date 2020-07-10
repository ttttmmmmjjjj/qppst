package com.hsic.picture;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v4.util.LruCache;

import java.io.File;

public class ImageShowManager {

	private static ImageShowManager imageManager;
	private static Application myApp;
	private LruCache<String, Bitmap> mMemoryCache;
	private DiskLruCache mDiskCache;
	// Ӳ�̻�������Ĵ�С����������֮ǰ�������ͼƬ����ʾ�ٶ�
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20;
	// Ӳ�̻���������ļ�����
	private static final String DISK_CACHE_SUBDIR = "thumbnails";

	public static final int bitmap_width = 100;
	public static final int bitmap_height = 100;

	public static ImageShowManager from(Context context) {
		// �������ui�߳��У����׳��쳣
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}

		if (myApp == null) {
			myApp = (Application) context.getApplicationContext();
		}

		if (imageManager == null) {
			imageManager = new ImageShowManager(myApp);
		}
		//
		return imageManager;
	}

	/**
	 * ˽�еĹ�����Ϊ�˱��ֵ���ģʽ
	 */
	private ImageShowManager(Context context) {
		/********** ��ʼ���ڴ滺���� ******/
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

		/********* ��ʼ��Ӳ�̻����� *********/
		File cacheDir = DiskLruCache
				.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
		mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);

	}

	/**
	 * ��ͼƬ���������ػ���
	 * 
	 * @param key
	 * @param value
	 */
	public void putBitmapToDisk(String key, Bitmap value) {
		mDiskCache.put(key, value);
	}

	/**
	 * ��Ӳ�̻����ж�ȡͼ��
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFormDisk(String url) {
		return mDiskCache.get(url);
	}

	/**
	 * ���ڴ滺�����л�ȡbitmap�������ض���url
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromMemory(String url) {
		return mMemoryCache.get(url);
	}

	/**
	 * ���µ�ͼƬ������ڴ滺����
	 * 
	 * @param key
	 * @param value
	 */
	public void putBitmapToMemery(String key, Bitmap value) {
		mMemoryCache.put(key, value);

	}

}
