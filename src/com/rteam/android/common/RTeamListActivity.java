package com.rteam.android.common;

import java.util.ArrayList;

import com.rteam.android.HelpDialog;
import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.user.Register;
import com.rteam.android.user.Settings;
import com.rteam.api.base.IUserTokenStorage;
import com.rteam.api.common.NetworkUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class RTeamListActivity extends ListActivity {

	////////////////////////////////////////////////////////
	/// Child Implementation Methods
	
	protected boolean isSecure() { return true; }
	protected String getCustomTitle() { return "rTeam - messages"; }
	protected void initialize() {}
	protected void reInitialize() {}
	protected void destroy() {}
	protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() { return new ArrayList<SimpleMenuItem>(); }
	
	protected IUserTokenStorage getTokenStorage() { return AndroidTokenStorage.get(); }
	
	protected RTeamAnalytics _tracker;
	
	//////////////////////////////////////////////////////////
	//// Create/Resume
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_tracker = new RTeamAnalytics(this);
		
		if (ensureSecure()) {
			initialize();
			ensureOnline();
		}
		
		CustomTitle.setTitle(getCustomTitle());
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
	@Override
	protected void onStart() {
		super.onStart();
		_tracker.trackActivityStart(this);
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
		CustomTitle.setTitle(getCustomTitle());
		if (ensureSecure()) {
			reInitialize();
		}
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
	
	///////////////////////////////////////////////////////////////////////////////
	//// Menu
	
	protected HelpProvider getHelpProvider() { return null; }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        for (SimpleMenuItem item : getSecondaryMenuItems()) {
        	MenuItem menuItem = menu.add(item.getText());
        	if (item.hasIcon()) {
        		menuItem.setIcon(item.getIconId());
        	}
        	
        	menuItem.setOnMenuItemClickListener(item.clickHandler());
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
