package com.rteam.android.common;

import com.rteam.api.common.StringUtils;

import android.util.Log;

public class RTeamLog {

	public static final String LOG_TAG = "rTeam";
	public static class TagSuffix
	{
		private String Suffix = null;
		private boolean hasSuffix() { return !StringUtils.isNullOrEmpty(Suffix); }
		
		public TagSuffix() { this(null); }
		public TagSuffix(String suffix){
			Suffix = suffix;
		}
		
		public String getTag() { return String.format("%s%s%s", LOG_TAG, hasSuffix() ? "." : "", hasSuffix() ? Suffix : ""); } 
	}
	
	public static void i(String msg) { i(new TagSuffix(), msg); }
	public static void i(String msgFormat, Object... args) { i(new TagSuffix(), msgFormat, args); }
	public static void i(TagSuffix tagSuffix, String msgFormat, Object... args) { i(tagSuffix, String.format(msgFormat, args)); }
	public static void i(TagSuffix tagSuffix, String msg) { if (msg != null) Log.i(tagSuffix.getTag(), msg); }
	
	
	public static void w(String msg) { w(new TagSuffix(), msg); }
	public static void w(String msgFormat, Object... args) { w(new TagSuffix(), msgFormat, args); }
	public static void w(TagSuffix tagSuffix, String msgFormat, Object... args) { w(tagSuffix, String.format(msgFormat, args)); }
	public static void w(TagSuffix tagSuffix, String msg) { if (msg != null) Log.w(tagSuffix.getTag(), msg); }
		
	public static void e(String msg) { e(new TagSuffix(), msg); }
	public static void e(String msgFormat, Object... args) { e(new TagSuffix(), msgFormat, args); }
	public static void e(TagSuffix tagSuffix, String msgFormat, Object... args) { e(tagSuffix, String.format(msgFormat, args)); }
	public static void e(TagSuffix tagSuffix, String msg) { if (msg != null) Log.e(tagSuffix.getTag(), msg); }
	
	public static void d(String msg) { d(new TagSuffix(), msg); }
	public static void d(String msgFormat, Object... args) { d(new TagSuffix(), msgFormat, args); }
	public static void d(TagSuffix tagSuffix, String msgFormat, Object... args) { d(tagSuffix, String.format(msgFormat, args)); }
	public static void d(TagSuffix tagSuffix, String msg) { if (msg != null) Log.d(tagSuffix.getTag(), msg); }	
}
