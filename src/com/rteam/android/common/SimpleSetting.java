package com.rteam.android.common;

import java.util.ArrayList;
import java.util.List;

public enum SimpleSetting {	
	MyTeam("MyTeam"),
	AutoLogin("AutoLogin"),
	ShowAlerts("ShowAlerts"),
	SeenWizard("HasSeenWizard");
	
	private String _identifier;
	
	private SimpleSetting(String identifier) { 
		_identifier = identifier; 
	}
	
	private static boolean hasSettings() { return settings() != null; }
	private static SimpleSettings settings() { return SimpleSettings.get(); }
	
	public boolean exists() 		{ return hasSettings() && settings().exists(_identifier); }
	
	public String get() 			{ return hasSettings() ? settings().get(_identifier) : ""; }
	public boolean getBoolean() 	{ return hasSettings() ? settings().getBoolean(_identifier) : false; }
	public boolean getBoolean(boolean defaultValue) { return hasSettings() ? settings().getBoolean(_identifier, defaultValue) : defaultValue; }
	public int getInteger() 		{ return hasSettings() ? settings().getInteger(_identifier) : 0; }
	public double getDouble() 		{ return hasSettings() ? settings().getDouble(_identifier) : 0.0; }
	public List<String> getList()	{ return getList("|"); }
	public List<String> getList(String delim)	{ 
		List<String> list = new ArrayList<String>();
		for (String str : get().split(delim)) list.add(str);
		return list;
	}
	
	
	public void set(String value) 	{ if (hasSettings()) settings().set(_identifier, value); }
	public void set(boolean value) 	{ if (hasSettings()) settings().set(_identifier, value); }
	public void set(int value) 		{ if (hasSettings()) settings().set(_identifier, value); }
	public void set(double value) 	{ if (hasSettings()) settings().set(_identifier, value); }
}
