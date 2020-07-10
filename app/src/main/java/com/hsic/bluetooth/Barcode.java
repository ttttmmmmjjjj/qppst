package com.hsic.bluetooth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Administrator on 2019/5/15.
 */

public class Barcode {
    private static final int BLACK = 0xFF000000;

    /**
     * @param content  文本内容
     * @param qrWidth  条形码的宽度
     * @param qrHeight 条形码的高度
     * @return bitmap
     */
    public static Bitmap getBarcodeBitmap(String content, int qrWidth, int qrHeight) {
        content = content.trim();
        //文字的高度
        int mHeight = qrHeight / 4;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix result;
            try {
                result = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, qrWidth, mHeight * 3, hints);
            } catch (IllegalArgumentException iae) {
                return null;
            }
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : 0;
                }
            }
            Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            qrBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //大的bitmap
            Bitmap bigBitmap = Bitmap.createBitmap(width, qrHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bigBitmap);
            Rect srcRect = new Rect(0, 0, width, height);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(qrBitmap, srcRect, dstRect, null);
            Paint p = new Paint();
            p.setColor(android.graphics.Color.BLACK);
            p.setFilterBitmap(false);
            //字体大小
            p.setTextSize(mHeight);
            p.setTextScaleX(2);
            //开始绘制文本的位置
            canvas.translate(width / 8, mHeight);
            canvas.drawText(content, 0, content.length(), 0, height, p);
            return bigBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成二维码
     * @param content
     * @param QR_WIDTH
     * @param QR_HEIGHT
     * @return
     */
    public static Bitmap QRCode(String content, int QR_WIDTH, int QR_HEIGHT) {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
        // 下面这里按照二维码的算法，逐个生成二维码的图片，
        // 两个for循环是图片横列扫描的结果
        for (int y = 0; y < QR_HEIGHT; y++) {
            for (int x = 0; x < QR_WIDTH; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * QR_WIDTH + x] = 0xff000000;
                } else {
                    pixels[y * QR_WIDTH + x] = 0xffffffff;
                }
            }
        }
        // 生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
        return bitmap;
    }
}

