package com.rteam.android.common;

import android.view.MenuItem;

public class SimpleMenuItem {
		
	private MenuItem.OnMenuItemClickListener _clickHandler;
	public MenuItem.OnMenuItemClickListener clickHandler() { return _clickHandler; }
	
	private String _text;
	public String getText() { return _text; }
		
	private boolean _hasIcon;
	public boolean hasIcon() { return _hasIcon; }
	
	private int _iconId;
	public int getIconId() { return _iconId; }
	
	
	public SimpleMenuItem(String text, MenuItem.OnMenuItemClickListener clickHandler) {
		this(text, -1, false, clickHandler);
	}
	
	public SimpleMenuItem(String text, int iconId, MenuItem.OnMenuItemClickListener clickHandler) {
		this(text, iconId, true, clickHandler);
	}
	
	private SimpleMenuItem(String text, int iconId, boolean hasIcon, MenuItem.OnMenuItemClickListener clickHandler) {
		_text = text;
		_iconId = iconId;
		_hasIcon = hasIcon;
		_clickHandler = clickHandler;
	}
	
}
