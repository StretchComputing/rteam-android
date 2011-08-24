package com.rteam.android.events.common;

import java.util.ArrayList;

import com.rteam.android.R;
import com.rteam.api.business.Member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FanListAdapter extends BaseAdapter {
	
	////////////////////////////////////////////////////////////////
	//// Members
	
	private LayoutInflater _inflater;
	private ArrayList<Member> _fans;

	////////////////////////////////////////////////////////////////
	//// .ctor
	
	public FanListAdapter(Context context, ArrayList<Member> fans) {
		_inflater = LayoutInflater.from(context);
		_fans = fans;
	}
	
	////////////////////////////////////////////////////////////////
	//// Overrides
	
	@Override public int getCount() { return _fans.size(); }
	@Override public Object getItem(int pos) { return _fans.get(pos); }
	@Override public long getItemId(int pos) { return pos; }
	@Override 
	public View getView(int pos, View convertView, ViewGroup group) {
		Member fan = (Member) getItem(pos);
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = _inflater.inflate(R.layout.list_fan, null);
			holder = new ViewHolder();
			holder.imageFan = (ImageView) convertView.findViewById(R.id.imageMain);
			holder.fanName = (TextView) convertView.findViewById(R.id.txtFan);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.fanName.setText(fan.memberName());
		return convertView;
	}
	
	static class ViewHolder {
		ImageView imageFan;
		TextView fanName;
	}

}
