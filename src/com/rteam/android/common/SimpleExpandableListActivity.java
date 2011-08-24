package com.rteam.android.common;

import com.rteam.android.R;
import java.util.ArrayList;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

public abstract class SimpleExpandableListActivity extends ExpandableListActivity {
	
	private ArrayList<SimpleListGroup> _groups;
	
	public SimpleExpandableListActivity() {
		_groups = new ArrayList<SimpleListGroup>();
	}
	
	
	private SimpleListGroup getGroup(String name) {
		for(SimpleListGroup group : _groups) {
			if (group.getText() == name) {
				return group;
			}
		}
		
		return null;
	}
	private boolean hasGroup(String name) { return getGroup(name) != null; }
	
	protected void addGroup(String groupText) {
		if (!hasGroup(groupText)) {
			_groups.add(new SimpleListGroup(groupText));
		}
	}
	protected void addItem(String group, String text, SimpleExpandableListClickListener clickHandler) {
		addItem(group, new SimpleListItem(text, clickHandler));
	}
	protected void addCheckItem(String group, String text, boolean isChecked, SimpleExpandableListClickListener clickHandler) {
		addItem(group, new SimpleListItem(text, isChecked, clickHandler));
	}
	protected void addItem(String group, SimpleListItem item) {
		addGroup(group);
		getGroup(group).getItems().add(item);
	}
	
	
	protected abstract void loadListItems();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
	}
	
	private void initialize() {
		loadListItems();
		setListAdapter(new SimpleListAdapter(this, _groups));
		afterInitialize();
	}
	
	protected void afterInitialize() {}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		SimpleListItem item = _groups.get(groupPosition).getItems().get(childPosition);
		
		// Update the checkbox
		if (item.hasCheckbox()) {
			item.setIsChecked(((CheckBox) v.findViewById(R.id.checkMain)).isChecked());
		}
		
		if (item.getListener() != null) {
			item.getListener().onClick(item);
		}
		
		return true;
	}
	
}
