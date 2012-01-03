package com.rteam.android.common;

import com.rteam.android.R;
import com.rteam.api.common.StringUtils;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomTitle {
	
	
	////////////////////////////////////////////////////////////////////////////////////
	//// Helper Class
	
	private static Runnable _clearCustomTitle = new Runnable() {
		public void run() {
			CustomTitle.setLoading(false);
			if (_timer != null) {
				_timer.removeCallbacks(_clearCustomTitle);
			}
		}
	};
	
	////////////////////////////////////////////////////////////////////////////////////
	//// Static Members
	
	private static final int AUTO_CANCEL_TIMEOUT = 5000;
	
	private static String _title;
	private static boolean _loading;
	private static String _loadingMessage;
	
	private static Handler _timer;
	
	private static CustomTitle _instance;
	public static boolean hasInstance() { return _instance != null; }
	public static void setInstance(CustomTitle instance) {
		if (instance != null) {
			_instance = instance; 
			_instance.bindTitle(null);
		}
		setupClearLoading();
	}
	
	public static void setTitle(String title) {
		_title = title;
		if (hasInstance()) {
			_instance.bindTitle(null);
		}
	}
	
	public static void setLoading(boolean loading) { setLoading(loading, null); }
	public static void setLoading(boolean loading, String loadingMessage) {
		_loading = loading;
		_loadingMessage = loadingMessage;
		if (hasInstance()) _instance.bindTitle(null);
		setupClearLoading();
	}
	
	private static void setupClearLoading() {
		if (_timer != null) {
			_timer.removeCallbacks(_clearCustomTitle);
		}
		if (_loading) {
			_timer = new Handler();
			_timer.postDelayed(_clearCustomTitle, AUTO_CANCEL_TIMEOUT);
		}
	}
		
	////////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	private TextView _lblTitle;
	private TextView _lblLoadingMessage;
	private ProgressBar _loadingProgress;	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	//// .ctor
	
	public CustomTitle(Activity activity) {
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
	    _lblTitle = (TextView) activity.findViewById(R.id.lblTitle);
        _lblLoadingMessage = (TextView) activity.findViewById(R.id.lblLoadingMessage);
        _loadingProgress = (ProgressBar) activity.findViewById(R.id.loadingProgress);
        
        bindTitle(activity);
	}
	
	
	private void bindTitle(Activity activity) {
		if (_lblTitle != null) 		_lblTitle.setText(_title);
		else if (activity != null) activity.setTitle(_title);
		
		if (_lblLoadingMessage != null && _loadingProgress != null) {
			_loadingProgress.setVisibility(_loading ? View.VISIBLE : View.GONE);
			_lblLoadingMessage.setVisibility((_loading && !StringUtils.isNullOrEmpty(_loadingMessage)) ? View.VISIBLE : View.GONE);
			_lblLoadingMessage.setText(_loadingMessage);
		}
	}
}
