package com.rteam.android.common;

import com.rteam.android.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public abstract class RTeamTabActivity extends TabActivity {
		
	private CustomTitle _titleInstance;
	protected abstract TabInfo[] getTabs(); 
		
	// TODO : Add security to these.
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    boolean customTitleSupported = false;
		try {
			customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		} catch(Exception e) {}
		
	    
	    setContentView(R.layout.tablayout);
	    
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
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
	
	protected void initialize() {}
	
	@Override
	public void onStart() {
		super.onStart();
		RTeamLog.d("rTeam Tab Activity - onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RTeamLog.d("rTeam Tab Activity - onResume");
		CustomTitle.setInstance(_titleInstance);
	}
}
