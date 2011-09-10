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
	
	public boolean exists() 		{ return SimpleSettings.get().exists(_identifier); }
	
	public String get() 			{ return SimpleSettings.get().get(_identifier); }
	public boolean getBoolean() 	{ return SimpleSettings.get().getBoolean(_identifier); }
	public boolean getBoolean(boolean defaultValue) { return SimpleSettings.get().getBoolean(_identifier, defaultValue); }
	public int getInteger() 		{ return SimpleSettings.get().getInteger(_identifier); }
	public double getDouble() 		{ return SimpleSettings.get().getDouble(_identifier); }
	public List<String> getList()	{ return getList("|"); }
	public List<String> getList(String delim)	{ 
		List<String> list = new ArrayList<String>();
		for (String str : get().split(delim)) list.add(str);
		return list;
	}
	
	
	public void set(String value) 	{ SimpleSettings.get().set(_identifier, value); }
	public void set(boolean value) 	{ SimpleSettings.get().set(_identifier, value); }
	public void set(int value) 		{ SimpleSettings.get().set(_identifier, value); }
	public void set(double value) 	{ SimpleSettings.get().set(_identifier, value); }
}
