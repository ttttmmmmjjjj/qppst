package com.hsic.picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureHelper {
/**
 * 
 * @param srcPath
 * @param desPath
 * ͨ��ͼƬ����ѹ��ͼƬ
 */
	public static void compressPicture(String srcPath, String desPath,int heigh,int width) {
		File file = null;
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
		op.inJustDecodeBounds = false;
		float w = op.outWidth;
		float h = op.outHeight;
		float hh = heigh;//
		float ww = width;//
		float be = 1.0f;
		if (w > h && w > ww) {
			be = (float) (w / ww);
		} else if (w < h && h > hh) {
			be = (float) (h / hh);
		}
		if (be <= 0) {
			be = 1.0f;
		}
		op.inSampleSize = (int) be;//
		bitmap = BitmapFactory.decodeFile(srcPath, op);
		int desWidth = (int) (w / be);
		int desHeight = (int) (h / be);
		bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
		try {
			file = new File(desPath);
			if (bitmap != null) {
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
				compressBmpToFile(bitmap,file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/**
 * 
 * @param bmp
 * @param file
 * ͨ��������ѹ��ͼƬѹ����100kһ��
 */
	public static void compressBmpToFile(Bitmap bmp, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;//
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > 350) {
			baos.reset();
			options -= 10;
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param
	 * @param
	 * ͨ��������ѹ��ͼƬѹ����ָ����С
	 */

	public void compressAndGenImage(Bitmap image, String outPath, int maxSize)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while (os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 10;
			
			if(options<=0){
				
				break;
			}
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}

		// Generate compressed image file
		FileOutputStream fos = new FileOutputStream(outPath);
		fos.write(os.toByteArray());
		fos.flush();
		fos.close();
	}
/**
 * ͨ��·����ȡBitmap
 * @param path
 * @return
 */
	public static Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		if (path != null && !path.equals("")) {
			bitmap = BitmapFactory.decodeFile(path);
		}
		return bitmap;
	}
}

