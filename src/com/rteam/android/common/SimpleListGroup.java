package com.rteam.android.common;

import java.util.ArrayList;

public class SimpleListGroup {
	
	private ArrayList<SimpleListItem> _items;
	private String _text;
	
	public ArrayList<SimpleListItem> getItems() { return _items; }
	public String getText() { return _text; }
	
	public SimpleListGroup(String text) {
		_text = text;
		_items = new ArrayList<SimpleListItem>();
	}
	
	public SimpleListGroup(String text, ArrayList<SimpleListItem> items) {
		_text = text;
		_items = items;
	}
	
	public SimpleListGroup(String text, SimpleListItem[] items) {
		_text = text;
		_items = new ArrayList<SimpleListItem>();
		for (SimpleListItem item : items) {
			_items.add(item);
		}
	}
}
