package com.hsic.utils;

import android.util.Log;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by Administrator on 2019/10/21.
 */

public class DESEncrypt
{
    Key key;
    IvParameterSpec iv;//向量
    SecretKey secretKey;

    public static void main(String[] args)
    {
        DESEncrypt des = new DESEncrypt();// 实例化一个对像
        des.getKey("aadd");// 生成密匙

        String strEnc1 = des.getEncString("钟汉康");// 加密字符串,返回String的密文
        String strEnc2 = des.getEncString("钟汉康");
        System.out.println(strEnc1);
        System.out.println(strEnc2);

        String strDes = des.getDesString(strEnc1);// 把String 类型的密文解密
        System.out.println(strDes);

    }

    /**
     * 根据参数生成KEY
     *
     * @param strKey
     */
    public void getKey2(String strKey)
    {
        try
        {
            KeyGenerator _generator = KeyGenerator.getInstance("DES");
            _generator.init(new SecureRandom(strKey.getBytes()));
            this.key = _generator.generateKey();
            _generator = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 根据参数生成KEY
     *
     * @param strKey
     */
    public void getKey(String strKey)
    {
        try
        {
            DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            secretKey = keyFactory.generateSecret(desKeySpec);
            iv = new IvParameterSpec(strKey.getBytes("UTF-8"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 加密String明文输入,String密文输出
     *
     * @param strMing
     * @return
     */
    public String getEncString(String strMing)
    {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        BASE64Encoder base64en = new BASE64Encoder();
        try
        {
            byteMing = strMing.getBytes("UTF8");
            byteMi = this.getEncCode(byteMing);
            strMi = base64en.encode(byteMi);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            base64en = null;
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }

    /**
     * 解密 以String密文输入,String明文输出
     *
     * @param strMi
     * @return
     */
    public String getDesString(String strMi)
    {
        BASE64Decoder base64De = new BASE64Decoder();
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try
        {
            byteMi = base64De.decodeBuffer(strMi);
            byteMing = this.getDesCode(byteMi);
            strMing = new String(byteMing, "UTF8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            base64De = null;
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以byte[]明文输入,byte[]密文输出
     *
     * @param byteS
     * @return
     */
    private byte[] getEncCode(byte[] byteS)
    {
        byte[] byteFina = null;
        Cipher cipher;
        try
        {
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,iv);
            byteFina = cipher.doFinal(byteS);
        }
        catch (Exception e)
        {
            Log.e("getEncCode",e.toString());
            e.printStackTrace();
        }
        finally
        {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以byte[]密文输入,以byte[]明文输出
     *
     * @param byteD
     * @return
     */
    private byte[] getDesCode(byte[] byteD)
    {
        Cipher cipher;
        byte[] byteFina = null;
        try
        {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byteFina = cipher.doFinal(byteD);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            cipher = null;
        }
        return byteFina;

    }

}
