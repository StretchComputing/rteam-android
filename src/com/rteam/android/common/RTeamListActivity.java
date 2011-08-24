package com.rteam.android.common;

import java.util.ArrayList;

import com.rteam.android.HelpDialog;
import com.rteam.android.R;
import com.rteam.android.user.Settings;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class RTeamListActivity extends ListActivity {

	////////////////////////////////////////////////////////
	/// Child Implementation Methods
	
	protected String getCustomTitle() { return "rTeam - messages"; }
	protected abstract void initialize();
	
	protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() { return new ArrayList<SimpleMenuItem>(); }
	
	// TODO : Add security 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		CustomTitle.setTitle(getCustomTitle());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		CustomTitle.setTitle(getCustomTitle());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		CustomTitle.setTitle(getCustomTitle());
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
		}
					
		return super.onMenuItemSelected(featureId, item);
	}
}
