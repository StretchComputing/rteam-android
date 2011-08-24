package com.rteam.api.common;

public class StringUtils {
	
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}
	
	public static String truncate(String value, int maxLength) {
		return truncate(value, maxLength, "...");
	}
	
	public static String truncate(String value, int maxLength, String suffix) {
		return (value == null || value.length() <= maxLength)
					? value
					: value.substring(0, maxLength - suffix.length()) + suffix;
	}
	
	public static String valueOr(String value, String defaultValue) {
		return !isNullOrEmpty(value)
					? value
					: defaultValue;
	}
}
