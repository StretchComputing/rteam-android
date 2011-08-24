package com.rteam.api.common;

public class EnumUtils {
	
	public static <T extends Enum<T>> T fromString(Class<T> enumType, String name) {
		return fromString(enumType, name, null);
	}
	
	public static <T extends Enum<T>> T fromString(Class<T> enumType, String name, T defaultValue) {
		if (StringUtils.isNullOrEmpty(name)) {
			return defaultValue;
		}
		
		try {
			return Enum.valueOf(enumType, name);
		}
		catch(Exception e) {}
		
		try {
			T[] types = enumType.getEnumConstants();
			if (types != null) {
				for(T type : types) {
					if (type.toString().equalsIgnoreCase(name)) return type;
				}
			}
		}
		catch(Exception e) {}
		return defaultValue;
	}
	
}