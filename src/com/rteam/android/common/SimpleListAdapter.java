package com.rteam.android.common;

import java.util.ArrayList;

import com.rteam.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SimpleListAdapter extends BaseExpandableListAdapter {
	private LayoutInflater _inflater;
	private ArrayList<SimpleListGroup> _groups;
	
	public SimpleListAdapter(Context context, ArrayList<SimpleListGroup> groups) {
		_groups = groups;
		_inflater = LayoutInflater.from(context);
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return _groups.get(groupPosition).getItems().get(childPosition);
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ItemViewHolder holder = null;
		SimpleListItem item = (SimpleListItem) getChild(groupPosition, childPosition);
		
		if (convertView == null) {
			convertView = _inflater.inflate(R.layout.list_simple_item, null);
			
			holder = new ItemViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.textMain);
			holder.check = (CheckBox) convertView.findViewById(R.id.checkMain);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}
		
		holder.text.setText(item.getText());
		holder.check.setChecked(item.isChecked());
		holder.check.setVisibility(item.hasCheckbox() ? View.VISIBLE : View.INVISIBLE);
		
		return convertView;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		return _groups.get(groupPosition).getItems().size();
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		return _groups.get(groupPosition);
	}
	
	@Override
	public int getGroupCount() {
		return _groups.size();
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		SimpleListGroup group = (SimpleListGroup) getGroup(groupPosition);
		GroupViewHolder holder = null;
		
		if (convertView == null) {
			convertView = _inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
			
			holder = new GroupViewHolder();
			holder.text = (TextView) convertView.findViewById(android.R.id.text1);
			
			convertView.setTag(holder);
		}
		else {
			holder = (GroupViewHolder) convertView.getTag();
		}
	
		holder.text.setText(group.getText());
		return convertView;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
	static class GroupViewHolder {
		TextView text;
	}
	
	static class ItemViewHolder {
		TextView text;
		CheckBox check;
	}
}
