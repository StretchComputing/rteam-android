package com.rteam.android.teams.common;

import java.util.ArrayList;

import com.rteam.android.R;
import com.rteam.api.business.EventBase;
import com.rteam.api.common.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {
	
	public static interface EventCheckedHandler {
		public void onCheck(int index, long id, boolean checked);
	}
	
	public static interface EventClickedHandler {
		public void onClick(int index, long id);
	}
	
	private ArrayList<EventBase> _events;
	private LayoutInflater _inflater;
	private EventCheckedHandler _checkHandler;
	private EventClickedHandler _clickHandler;
	private boolean _canEdit;
	
	public EventListAdapter(Context context, boolean canEdit, ArrayList<EventBase> events, EventCheckedHandler checkHandler, EventClickedHandler clickHandler) {
		_inflater = LayoutInflater.from(context);
		_events = events;
		_checkHandler = checkHandler;
		_clickHandler = clickHandler;
		_canEdit = canEdit;
	}
	

	@Override
	public int getCount() {
		return _events.size();
	}

	@Override
	public Object getItem(int position) {
		return _events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EventBase event = (EventBase) getItem(position);
		final int pos = position;
		
		convertView = _inflater.inflate(R.layout.teams_events_item, null);
		
		CheckBox checkSelected = (CheckBox) convertView.findViewById(R.id.checkSelected);
		TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
		TextView txtType = (TextView) convertView.findViewById(R.id.txtType);
		TextView txtOpponent = (TextView) convertView.findViewById(R.id.txtOpponent);
		TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
					
		checkSelected.setVisibility(_canEdit ? View.VISIBLE : View.GONE);
		checkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_checkHandler.onCheck(pos, getItemId(pos), isChecked);
			}
		});
		
		txtDate.setText(DateUtils.toPrettyString(event.startDate()));
		txtType.setText(event.eventType().toPrettyString());
		txtOpponent.setText("vs. " + event.opponent());
		txtDescription.setText(event.description());
		
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_clickHandler.onClick(pos, getItemId(pos));
			}
		});
			
		return convertView;
	}
}
