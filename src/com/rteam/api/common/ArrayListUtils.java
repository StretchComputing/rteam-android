package com.rteam.api.common;

import java.util.ArrayList;

public class ArrayListUtils {

	public static interface GetString<T> {
		public String getString(T obj);
	}
	
	
	public static String toString(ArrayList array) {
		return toString(array, ",");
	}
	
	public static String toString(ArrayList array, String delimiter) {
		return toString(array, delimiter, new GetString() {
			@Override
			public String getString(Object obj) {
				return obj.toString();
			}
		});
	}
	
	public static <T> String toString(ArrayList<T> array, String delimiter, GetString<T> getString) {
		String compressed = "";
		if (array != null && array.size() > 0) {
			boolean first = true;
			for(T item : array) {
				compressed += (first ? "" : delimiter) + getString.getString(item);
				first = false;
			}
		}
		return compressed;
	}
	
}