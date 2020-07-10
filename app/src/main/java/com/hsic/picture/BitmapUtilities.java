package com.hsic.picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapUtilities {

	public BitmapUtilities() {
		// TODO Auto-generated constructor stub
	}

	public static Bitmap getBitmapThumbnail(String path, int width, int height) {
		Bitmap bitmap = null;
		// ������԰�������СͼƬ��
		/*
		 * BitmapFactory.Options opts = new BitmapFactory.Options();
		 * opts.inSampleSize = 4;//��͸߶���ԭ����1/4 bitmap =
		 * BitmapFactory.decodeFile(path, opts);
		 */

		/*
		 * ��һ���ģ� �������ǡ����inSampleSize�ǽ��������Ĺؼ�֮һ��BitmapFactory.
		 * Options�ṩ����һ����ԱinJustDecodeBounds��
		 * ����inJustDecodeBoundsΪtrue��decodeFile��������ռ�
		 * �����ɼ����ԭʼͼƬ�ĳ��ȺͿ�ȣ���opts.width��opts.height��
		 * ������������������ͨ��һ�����㷨�����ɵõ�һ��ǡ����inSampleSize��
		 */
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = Math.max((int) (opts.outHeight / (float) height),
				(int) (opts.outWidth / (float) width));
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, opts);
		return bitmap;
	}

	public static Bitmap getBitmapThumbnail(Bitmap bmp, int width, int height) {
		Bitmap bitmap = null;
		if (bmp != null) {
			int bmpWidth = bmp.getWidth();
			int bmpHeight = bmp.getHeight();
			if (width != 0 && height != 0) {
				Matrix matrix = new Matrix();
				float scaleWidth = ((float) width / bmpWidth);
				float scaleHeight = ((float) height / bmpHeight);
				matrix.postScale(scaleWidth, scaleHeight);
				bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
						matrix, true);
			} else {
				bitmap = bmp;
			}
		}
		return bitmap;
	}

}
