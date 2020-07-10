package com.hsic.permission;

import android.content.Context;

/**
 * Created by Administrator on 2018/11/28.
 */

public class ProviderUtil {
    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
