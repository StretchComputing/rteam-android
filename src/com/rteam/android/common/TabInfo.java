package com.rteam.android.common;

public class TabInfo {
	private Class<?> _intentClass;
	private String _identifier;
	private String _textBelow;
	private int _iconId;
	
	public Class<?> getIntentClass() { return _intentClass; }
	public String getIdentifier() { return _identifier; }
	public String getTextBelow() { return _textBelow; }
	public int getIconId() { return _iconId; }
	
	public TabInfo(Class<?> intentClass, String identifier, String textBelow, int iconId) {
		_intentClass = intentClass;
		_identifier = identifier;
		_textBelow = textBelow;
		_iconId = iconId;
	}
}
