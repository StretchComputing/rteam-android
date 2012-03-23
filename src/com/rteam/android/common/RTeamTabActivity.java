package com.rteam.android.common;

import com.rteam.android.Home;
import com.rteam.android.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;

public abstract class RTeamTabActivity extends TabActivity {
		
	private CustomTitle _titleInstance;
	protected abstract TabInfo[] getTabs(); 
	protected void destroy() {}
	
	protected RTeamAnalytics _tracker;
		
	// TODO : Add security to these.
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    _tracker = new RTeamAnalytics(this);
	    boolean customTitleSupported = false;
		try {
			customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		} catch(Exception e) {}
		
	    
	    setContentView(R.layout.tablayout);
	    
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    TabInfo[] tabs = getTabs();
	    
	    if (tabs.length > 1) {
		    for(TabInfo tab : getTabs()) {
		    	intent = new Intent(this, tab.getIntentClass());
			    spec = tabHost.newTabSpec(tab.getIdentifier()).setIndicator(tab.getTextBelow(), res.getDrawable(tab.getIconId())).setContent(intent);
			    tabHost.addTab(spec);
		    }   
		    initialize();
		    
		    if (customTitleSupported) {
		    	_titleInstance = new CustomTitle(this);
		    	CustomTitle.setInstance(_titleInstance);
		    }
	    }
	    else if (tabs.length == 1) {
	    	finish();
	    	startActivity(new Intent(this, tabs[0].getIntentClass()));
	    }
	    else {
	    	finish();
	    	startActivity(new Intent(this, Home.class));
	    }
	}
	
	protected void initialize() {}
	
	@Override
	public void onStart() {
		super.onStart();
		_tracker.trackActivityStart(this);
		RTeamLog.d("rTeam Tab Activity - onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		_tracker.trackActivityResume(this);
		RTeamLog.d("rTeam Tab Activity - onResume");
		CustomTitle.setInstance(_titleInstance);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		_tracker.trackActivityStop(this);
		destroy();
	}	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_tracker.dispose();
		cleanupUI();
	}
	
	private void cleanupUI() {
	    System.gc();
	    Runtime.getRuntime().gc();
	    unbindDrawables(findViewById(android.R.id.content));
	}
	
	private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }

            try {
            	((ViewGroup) view).removeAllViews();
            } catch(Exception ex) {
            	// do nothing
            }
        }
	}
}
