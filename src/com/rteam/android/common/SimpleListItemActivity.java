package com.rteam.android.common;

import android.content.Context;
import android.content.Intent;

public class SimpleListItemActivity extends SimpleListItem {

	private Context _context;
	private Class<?> _intentClass;
	
	public SimpleListItemActivity(String text, Context context, Class<?> intentClass) {
		super(text, new SimpleExpandableListClickListener() {
			public void onClick(SimpleListItem item) { clicked((SimpleListItemActivity) item); }
		});
		
		_context = context; 
		_intentClass = intentClass;
	}
	
	
	private static void clicked(SimpleListItemActivity caller) { caller.clicked(); }
	private void clicked() { 
		_context.startActivity(new Intent(_context, _intentClass));
	}
}
