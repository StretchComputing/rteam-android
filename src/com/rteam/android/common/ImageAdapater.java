package com.rteam.android.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapater extends BaseAdapter
{
	private Context _context;
	private int[] _imageIDs;
	
	private final int _width;
	private final int _height;
	
	public ImageAdapater(Context context, int[] imageIDs, int width, int height) {
		_context = context;
		_imageIDs = imageIDs;
		_width = width;
		_height = height;
	}

	@Override
	public int getCount() {
		return _imageIDs.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return _imageIDs[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_context);
            imageView.setLayoutParams(new GridView.LayoutParams(_width, _height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(_imageIDs[position]);
        return imageView;
	}
}
