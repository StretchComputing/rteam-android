package com.rteam.api.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	public static ArrayList<String> convertToStrings(JSONArray array) {
		ArrayList<String> strings = new ArrayList<String>();
		if (array != null) {
			for(int i=0; i<array.length(); i++) {
				strings.add(array.optString(i));
			}
		}
		return strings;
	}
	
	public static JSONArray convertToArray(Collection<String> strings) {
		if (strings == null) return null;
		JSONArray array = new JSONArray();
		for(String str : strings) {
			array.put(str);
		}
		return array;
	}
	
	public static Date parseDate(String str) { return parseDate(str, new Date()); }
	public static Date parseDate(String str, Date defaultValue) {
		if (str != null) {
			try {
				return new Date(str);
			} catch(Exception e) {}
		}
		return defaultValue;
	}
	
	
	
	public static void put(JSONObject json, String name, String value) {
		if (!StringUtils.isNullOrEmpty(value)) {
			try { 
				json.putOpt(name, value); 
			} catch (JSONException e) {}
		}
	}
}
