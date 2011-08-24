package com.rteam.android.common;

public class SimpleListItem {
	
	protected String _text;	
	protected boolean _hasCheckbox;
	protected boolean _isChecked;
	protected Object _tag;
	
	protected SimpleExpandableListClickListener _listener;

	public String getText() { return _text; }
	public boolean hasCheckbox() { return _hasCheckbox; }
	public boolean isChecked() { return _isChecked; }
	public void setIsChecked(boolean isChecked) { _isChecked = isChecked; }
	public Object getTag() { return _tag; }
	
	public SimpleExpandableListClickListener getListener() { return _listener; }
	
	public SimpleListItem(String text, SimpleExpandableListClickListener listener) {
		this(text, null, listener);
	}
	public SimpleListItem(String text, Object tag, SimpleExpandableListClickListener listener) {
		this(text, false, false, tag, listener);
	}
	
	public SimpleListItem(String text, boolean isChecked, SimpleExpandableListClickListener listener) {
		this(text, isChecked, null, listener);
	}
	public SimpleListItem(String text, boolean isChecked, Object tag, SimpleExpandableListClickListener listener) {
		this(text, true, isChecked, tag, listener);
	}
	
	private SimpleListItem(String text, boolean hasCheckbox, boolean isChecked, Object tag, SimpleExpandableListClickListener listener) {
		_text = text;
		_hasCheckbox = hasCheckbox;
		_isChecked = isChecked;
		_listener = listener;
		_tag = tag;
	}
}
