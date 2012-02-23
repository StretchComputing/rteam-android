package com.rteam.android.common;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;

import com.rteam.android.HelpDialog;
import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.user.Settings;
import com.rteam.android.user.Register;
import com.rteam.api.base.IUserTokenStorage;
import com.rteam.api.common.NetworkUtils;

public abstract class RTeamActivity extends Activity {

	///////////////////////////////////////////////////////////////////////////////
	//// Members
	
	protected IUserTokenStorage getTokenStorage() { return AndroidTokenStorage.get(); }
	
	///////////////////////////////////////////////////////////////////////////////
	//// Abstract/Virtual Methods	
	
	protected boolean isSecure() { return true; }
	protected void initialize() {}
	protected void reInitialize() {}
	protected void destroy() {}
	protected String getCustomTitle() { return "rTeam - custom title"; }
	protected boolean showMenu() { return true; }
	
	protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() { return new ArrayList<SimpleMenuItem>(); }

	protected HelpProvider getHelpProvider() { return null; }
	
	protected boolean showHomeButton() { return true; }
	private CustomTitle _titleInstance;
	
	protected RTeamAnalytics _tracker;
			
	///////////////////////////////////////////////////////////////////////////////
	//// Create
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_tracker = new RTeamAnalytics(this);
		
		boolean customTitleSupported = false;
		try {
			customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		} catch(Exception e) {}
		
		if (ensureSecure()) {
			initialize();
			ensureOnline();
		}
		
		if (customTitleSupported) {
			_titleInstance = new CustomTitle(this);
			CustomTitle.setInstance(_titleInstance);
			CustomTitle.setTitle(getCustomTitle());
	    }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		_tracker.trackActivityStart(this);
		RTeamLog.d("rTeam Activity - onStart");
		CustomTitle.setTitle(getCustomTitle());
		if (ensureSecure()) {
			reInitialize();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		_tracker.trackActivityStop(this);
		destroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		_tracker.trackActivityResume(this);
		RTeamLog.d("rTeam Activity - onResume");
		CustomTitle.setInstance(_titleInstance);
		CustomTitle.setTitle(getCustomTitle());
		if (ensureSecure()) {
			reInitialize();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_tracker.dispose();
	}
	
	
	private boolean ensureSecure() {
		if (isSecure() && !getTokenStorage().hasUserToken()) {
			Intent register = new Intent(this, Register.class);
			startActivity(register);
			return false;
		}
		return true;
	}
		
	private void ensureOnline() {
		if (!NetworkUtils.isOnline(this)) {
			if (isFinishing()) return;
			new AlertDialog.Builder(this)
					.setTitle("Error!")
					.setMessage("Error, internet access is required to run rTeam.  Please ensure that you are connected to the internet to continue using rTeam.")
					.setPositiveButton("OK", null)
					.show();
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	//// Menu
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (showMenu()) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main, menu);
	        MenuItem home = menu.findItem(R.id.btnHome);
	        home.setVisible(showHomeButton());
	        
	        for (SimpleMenuItem item : getSecondaryMenuItems()) {
	        	MenuItem menuItem = menu.add(item.getText());
	        	if (item.hasIcon()) {
	        		menuItem.setIcon(item.getIconId());
	        	}
	        	
	        	menuItem.setOnMenuItemClickListener(item.clickHandler());
	        }
		}	        
        return true;
    }
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnHelp:
			new HelpDialog(this, getHelpProvider()).showDialog();
			return true;
		case R.id.btnSettings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.btnHome:
			startActivity(new Intent(this, Home.class));
			return true;
		}
					
		return super.onMenuItemSelected(featureId, item);
	}
}

