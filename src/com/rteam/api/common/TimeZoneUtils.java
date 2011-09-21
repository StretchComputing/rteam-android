package com.rteam.api.common;

import java.util.Locale;
import java.util.TimeZone;

public class TimeZoneUtils {
	public static String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
	
	public static String getShortTimeZone(TimeZone timeZone) {
		return timeZone.getDisplayName(false, TimeZone.SHORT, Locale.getDefault());
	}
	
	public static String getLongTimeZone(TimeZone timeZone) {
		return timeZone.getDisplayName(false, TimeZone.LONG, Locale.getDefault());
	}
}
