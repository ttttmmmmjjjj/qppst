package com.hsic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
	/**
	 * ��ȡ��ǰʱ��
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getTime(String pattern) {
		String time = "";
		Date date = new Date();
		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat(pattern,
				Locale.CHINA);
		time = SimpleDateFormat.format(date);
		return time;
	}

	/**
	 * @param beginDateStr
	 * @param endDateStr
	 * 20161018
	 * @return
	 */
	public static long getDaySub(SimpleDateFormat format,String beginDateStr, String endDateStr) {
		long day = 0;
		Date beginDate;
		Date endDate;
		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
			day = (endDate.getTime() - beginDate.getTime())
					/ (24 * 60 * 60 * 1000);
			// System.out.println("���������="+day);
		} catch (ParseException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
		return day;
	}
}
