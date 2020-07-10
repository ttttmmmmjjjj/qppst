package com.hsic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/8/24.
 */

public class DataUtils {
    public static String getCurrentTime(){
        String inspectedDate="2000-01-01 00:00:00";
        try{
            Date date = new Date();
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
                    Locale.CHINA);
            inspectedDate = simpleDateFormat.format(date);//获取安检日期
        }catch(Exception ex){
            ex.toString();
        }
        return inspectedDate;
    }
    public static String getCurrentDate(){
        String inspectedDate="2000-01-01";
        try{
            Date date = new Date();
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
                    Locale.CHINA);
            inspectedDate = simpleDateFormat.format(date);//获取安检日期
        }catch(Exception ex){
            ex.toString();
        }
        return inspectedDate;
    }
    public static String getDate(){
        String inspectedDate="20000101";
        try{
            Date date = new Date();
            String pattern = "yyyyMMdd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
                    Locale.CHINA);
            inspectedDate = simpleDateFormat.format(date);//获取安检日期
        }catch(Exception ex){
            ex.toString();
        }
        return inspectedDate;
    }

    /**
     * 检验手机号码
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            if (!isMatch) {
            }
            return isMatch;
        }
    }
}
