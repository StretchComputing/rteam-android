package com.rteam.api.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	private static SimpleDateFormat _todayShortFormat = new SimpleDateFormat("h:mm a");
	private static SimpleDateFormat _otherShortFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private static SimpleDateFormat _fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static SimpleDateFormat _prettyStringFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a");
	private static SimpleDateFormat _prettyStringFormat2 = new SimpleDateFormat("MMM dd, yyyy\nh:mm a");
	
	private static SimpleDateFormat _dateParameterFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static SimpleDateFormat _timeStringFormat = new SimpleDateFormat("h:mm a");
	
	public static String toShortString(Date date) {
		SimpleDateFormat formatter = isToday(date) ? _todayShortFormat : _otherShortFormat;
		return formatter.format(date);
	}
	
	public static boolean isAfterToday(Date date) {
		return new Date().compareTo(date) >= 0;
	}
	
	public static String toDateParameterString(Date date) { return _dateParameterFormat.format(date); }
	public static String toPrettyString(Date date) { return _prettyStringFormat.format(date); }
	public static String toPrettyString2(Date date) { return _prettyStringFormat2.format(date); }
	public static String toStringTime(Date date) { return _timeStringFormat.format(date); }
	
	public static boolean isToday(Date date) { return isSameDay(date, new Date()); }
	public static boolean isSameDay(Date date, Date date2) {
		return date.getDate() == date2.getDate()
				&& date.getMonth() == date2.getMonth()
				&& date.getYear() == date2.getYear();
	}
	
	
	public static String toFullString(Date date) {
		return _fullFormat.format(date);
	}
	
	public static Date parse(String dateString) {
		return parse(dateString, _fullFormat, new Date());
	}
	
	public static Date parse(String dateString, DateFormat format, Date defaultDate) {
		if (dateString == null || dateString.length() == 0) return defaultDate;

		try {
			Date parsed = format.parse(dateString);
			return parsed;
		} catch(ParseException e) {}
		return defaultDate;
	}
	
	
	public static boolean areSameDay(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.setTime(date1);
		c2.setTime(date2);
		
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
				&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
	}
}
