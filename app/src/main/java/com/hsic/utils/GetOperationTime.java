package com.hsic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public  class GetOperationTime {
	public static String getCurrentTime(){
		String inspectedDate="2000-01-01 00:00:00";
		try{
			Date date = new Date();
			String pattern = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
					Locale.getDefault());
			inspectedDate = simpleDateFormat.format(date);//��ȡѲ������
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
					Locale.getDefault());
			inspectedDate = simpleDateFormat.format(date);//��ȡѲ������
		}catch(Exception ex){
			ex.toString();
		}
		return inspectedDate;
	}
}
