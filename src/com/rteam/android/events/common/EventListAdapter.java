package com.rteam.android.events.common;

import java.util.ArrayList;

import com.rteam.android.R;
import com.rteam.api.business.EventBase;
import com.rteam.api.common.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {

	/////////////////////////////////////////////////////////////////
	//// Members

	private ArrayList<EventBase> _events;
	private LayoutInflater _inflater;
	
	/////////////////////////////////////////////////////////////////
	//// .ctor
	
	public EventListAdapter(Context context, ArrayList<EventBase> events) {
		_inflater = LayoutInflater.from(context);
		_events = events;
	}
	
	/////////////////////////////////////////////////////////////////
	//// Overrides

	
	@Override
	public int getCount() { return _events.size() + 1; }

	@Override
	public Object getItem(int position) { return position == 0 ? null : _events.get(position - 1); }

	@Override
	public long getItemId(int position) { return position; }

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		EventBase event = (EventBase) getItem(position);
		ViewHolder holder;		
		if (convertView == null) { 
			convertView = _inflater.inflate(R.layout.list_item_simple2, null);
			holder = new ViewHolder();
			holder.lblLine1 = (TextView) convertView.findViewById(R.id.lblLine1);
			holder.lblLine2 = (TextView) convertView.findViewById(R.id.lblLine2);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.lblLine1.setText(event != null 
									? event.eventType().toPrettyString() + " at " + DateUtils.toStringTime(event.startDate())
									: "*Create New Event*");
		holder.lblLine2.setText(event != null 
									? event.teamName()
									: "");
		
		return convertView;
	}
	
	static class ViewHolder {
		public TextView lblLine1;
		public TextView lblLine2;
	}

}
