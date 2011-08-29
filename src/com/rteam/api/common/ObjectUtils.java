package com.rteam.api.common;

public class ObjectUtils {
	public static <T> T notNull(T first, T fallback) {
		return first != null
				? first
				: fallback;
	}
}
