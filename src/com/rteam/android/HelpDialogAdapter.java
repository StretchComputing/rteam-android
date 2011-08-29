package com.rteam.android;

import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class HelpDialogAdapter extends BaseExpandableListAdapter {
	
	private HelpProvider _provider;
	private Context _context;
	
	public HelpDialogAdapter(HelpProvider provider, Context context) {
		_provider = provider;
		_context = context;
	}

	@Override public Object getChild(int group, int index) { return _provider.getHelp().get(group).getContent(); }
	@Override public long getChildId(int group, int index) { return group * 10 + index; }

	@Override public int getChildrenCount(int group) { return 1; }
	@Override public Object getGroup(int group) { return _provider.getHelp().get(group); }
	@Override public int getGroupCount() { return _provider.getHelp().size(); }
	@Override public long getGroupId(int group) { return group; }

	@Override public boolean hasStableIds() { return false; }
	@Override public boolean isChildSelectable(int arg0, int arg1) {return false;}

	@Override
	public View getGroupView(int group, boolean bool, View convertView, ViewGroup viewGroup) {
		HelpContent help = (HelpContent) getGroup(group);
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(_context).inflate(R.layout.list_simple_group, null);
			holder = new ViewHolder();
			holder.txtView = (TextView) convertView.findViewById(R.id.textMain);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.bind(help.getTitle());
		
		return convertView;
	}
	
	@Override
	public View getChildView(int group, int index, boolean bool, View convertView, ViewGroup viewGroup) {
		HelpContent help = (HelpContent) getGroup(group);
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(_context).inflate(R.layout.list_simple, null);
			holder = new ViewHolder();
			holder.txtView = (TextView) convertView.findViewById(R.id.txtString);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.bind(help.getContent());
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtView;
		
		public void bind(String text) { txtView.setText(text); }
	}


}
