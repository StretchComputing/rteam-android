package com.rteam.android.common;

import java.util.ArrayList;

import com.rteam.android.HelpDialog;
import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.user.Register;
import com.rteam.android.user.Settings;
import com.rteam.api.base.IUserTokenStorage;
import com.rteam.api.common.NetworkUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class RTeamActivityChildTab extends Activity {
	
	private Bundle _state;
	
	protected Bundle getState() { return _state; }
	protected boolean isSecure() { return true; }
	protected void initialize() {}
	
	protected IUserTokenStorage getTokenStorage() { return AndroidTokenStorage.get(); }
	
	protected String getCustomTitle() { return "rTeam - custom title"; }
	
	protected HelpProvider getHelpProvider() { return null; }

	protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() { return new ArrayList<SimpleMenuItem>(); }
	protected boolean showHomeButton() { return true; }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		_state = savedInstanceState;
		
		AndroidTokenStorage.initialize(this);		
		if (ensureSecure()) {		
			initialize();
			ensureOnline();
		}
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
			new AlertDialog.Builder(this)
					.setTitle("Error!")
					.setMessage("Error, internet access is required to run rTeam.  Please ensure that you are connected to the internet to continue using rTeam.")
					.setPositiveButton("OK", null)
					.show();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		RTeamLog.d("rTeam Activity - onStart");
		ensureSecure();
		CustomTitle.setTitle(getCustomTitle());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RTeamLog.d("rTeam Activity - onResume");
		CustomTitle.setTitle(getCustomTitle());
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//// Menu
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
