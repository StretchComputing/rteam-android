package com.rteam.android.common;

import java.util.ArrayList;

import com.rteam.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Simple3LineAdapater extends BaseAdapter {
	
	//////////////////////////////////////////////////////////////
	//// Helper Classes
	
	public static class Data {
		public String Line1;
		public String Line2;
		public String Line3;
		
		public Bitmap Image;
		
		public Data(String l1, String l2, String l3) { this(l1, l2, l3, null); }
		public Data(String l1, String l2, String l3, Bitmap image) {
			Line1 = l1;
			Line2 = l2;
			Line3 = l3;
			Image = image;
		}
	}
	
	//////////////////////////////////////////////////////////////
	//// Members
	
	private LayoutInflater _inflator;
	private ArrayList<Data> _data;
	
	//////////////////////////////////////////////////////////////
	//// .ctor
	
	public Simple3LineAdapater(Context context, ArrayList<Data> data) {
		_inflator = LayoutInflater.from(context);
		_data = data;
	}
	
	
	//////////////////////////////////////////////////////////////
	//// Overrides
	
	

	@Override
	public int getCount() { return _data.size(); }

	@Override
	public Object getItem(int index) { return _data.get(index); }

	@Override
	public long getItemId(int position) { return position; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Data data = (Data) getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = _inflator.inflate(R.layout.list_item_simple3, null);
			holder = new ViewHolder();
			holder.lblLine1 = (TextView) convertView.findViewById(R.id.lblLine1);
			holder.lblLine2 = (TextView) convertView.findViewById(R.id.lblLine2);
			holder.lblLine3 = (TextView) convertView.findViewById(R.id.lblLine3);
			holder.imageMain = (ImageView) convertView.findViewById(R.id.imageMain);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.lblLine1.setText(data.Line1);
		holder.lblLine2.setText(data.Line2);
		holder.lblLine3.setText(data.Line3);
		
		if (data.Image != null) {
			holder.imageMain.setImageBitmap(data.Image);
			holder.imageMain.setVisibility(View.VISIBLE);
		}
		else {
			holder.imageMain.setImageBitmap(null);
			holder.imageMain.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	
	static class ViewHolder {
		public TextView lblLine1;
		public TextView lblLine2;
		public TextView lblLine3;
		
		public ImageView imageMain;
	}
}
