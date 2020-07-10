package com.hsic.bll;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * Created by Administrator on 2018/8/16.
 */

public class SaleCode {
    private static final int BARCODE_WIDTH = 400;
    private static final int BARCODE_HEIGHT = 80;
    private String SaleID;
    public SaleCode(String SaleID){
        this.SaleID=SaleID;

    }
    public byte[] getSaleCode(){
        byte[] code=null;
        try{
            Bitmap bmp = encodeBarcode(SaleID,
                    BARCODE_WIDTH, BARCODE_HEIGHT);
            code= genBitmapCode(bmp, false, false);;
        }catch (Exception ex){

        }
        return code;
    }
    private static Bitmap encodeBarcode(String text, int width, int height)
            throws WriterException {

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix m = new MultiFormatWriter().encode(text,
                BarcodeFormat.CODE_128, width, height, hints);
        return matrixToBitmap(m);
    }
    public static Bitmap matrixToBitmap(BitMatrix matrix){
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;
        Bitmap bmp = Bitmap.createBitmap(resWidth, resHeight+10, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.WHITE);

        for (int y = 0; y < resHeight; y++) {
            for (int x = 0; x < resWidth; x++) {
                if (matrix.get(x + rec[0], y + rec[1]))
                    bmp.setPixel(x, y, Color.BLACK);
            }
        }
        return bmp;
    }
    public static final int MAX_BIT_WIDTH = 476;
    private static byte[] genBitmapCode(Bitmap bm, boolean doubleWidth, boolean doubleHeight) {
        int w = bm.getWidth();
        int h = bm.getHeight();
        if(w > MAX_BIT_WIDTH)
            w = MAX_BIT_WIDTH;
        int bitw = ((w+7)/8)*8;
        int bith = h;
        int pitch = bitw / 8;
        byte[] cmd = {0x1D, 0x76, 0x30, 0x00, (byte)(pitch&0xff), (byte)((pitch>>8)&0xff), (byte) (bith&0xff), (byte) ((bith>>8)&0xff)};
        byte[] bits = new byte[bith*pitch];

        // 倍宽
        if(doubleWidth)
            cmd[3] |= 0x01;
        // 倍高
        if(doubleHeight)
            cmd[3] |= 0x02;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = bm.getPixel(x, y);
                if ((color&0xFF) < 128) {
                    bits[y * pitch + x/8] |= (0x80 >> (x%8));
                }
            }
        }
        ByteBuffer bb = ByteBuffer.allocate(cmd.length+bits.length);
        bb.put(cmd);
        bb.put(bits);
        return bb.array();
    }

}
