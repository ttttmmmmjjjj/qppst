package com.hsic.utils;

import java.io.InputStream;

/**
 * Created by Administrator on 2019/6/14.
 */

public class StreamUtil {
    public static String readStream(InputStream is){
        StringBuffer out = new StringBuffer();
        try{

            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
        }catch(Exception ex){
            return "";
        }

       return  out.toString();

    }
}
