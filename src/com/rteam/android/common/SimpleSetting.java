package com.rteam.android.common;

import java.util.ArrayList;
import java.util.List;

public enum SimpleSetting {	
	MyTeam("MyTeam"),
	AutoLogin("AutoLogin"),
	ShowAlerts("ShowAlerts"),
	SeenWizard("HasSeenWizard");
	
	private SimpleSettings _settings;
	private String _identifier;
	
	private SimpleSetting(String identifier) { 
		_identifier = identifier; 
		_settings = SimpleSettings.get();
	}
	
	public boolean exists() 		{ return _settings.exists(_identifier); }
	
	public String get() 			{ return _settings.get(_identifier); }
	public boolean getBoolean() 	{ return _settings.getBoolean(_identifier); }
	public boolean getBoolean(boolean defaultValue) { return _settings.getBoolean(_identifier, defaultValue); }
	public int getInteger() 		{ return _settings.getInteger(_identifier); }
	public double getDouble() 		{ return _settings.getDouble(_identifier); }
	public List<String> getList()	{ return getList("|"); }
	public List<String> getList(String delim)	{ 
		List<String> list = new ArrayList<String>();
		for (String str : get().split(delim)) list.add(str);
		return list;
	}
	
	
	public void set(String value) 	{ _settings.set(_identifier, value); }
	public void set(boolean value) 	{ _settings.set(_identifier, value); }
	public void set(int value) 		{ _settings.set(_identifier, value); }
	public void set(double value) 	{ _settings.set(_identifier, value); }
}
