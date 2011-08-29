package com.rteam.android;

import com.rteam.android.common.SimpleSetting;
import com.rteam.android.teams.CreateTeam;
import com.rteam.api.common.NetworkUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

public class NewUserDialog {
	
	/////////////////////////////////////////////////////////////////////////////
	/// Members
	
	private Context _context;
	private View _view;

	/////////////////////////////////////////////////////////////////////////////
	/// .ctor
	
	public NewUserDialog(Context context) {
		_context = context;
		
		initializeView();
	}

	/////////////////////////////////////////////////////////////////////////////
	/// Functions
	
	private void initializeView() {
		_view = LayoutInflater.from(_context).inflate(R.layout.dlg_newuserwizard, null);
	}
	
	public void showDialog() {
		if (NetworkUtils.isOnline(_context)) {
			new AlertDialog.Builder(_context)
					.setTitle("Welcome to rTeam")
					.setView(_view)
					.setPositiveButton("Create a Team", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { createTeamClicked(); } })
					.setNegativeButton("Skip wizard", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { skipWizardClicked(); } })
					.setCancelable(false)
					.show();
		}
	}
	
	
	private void createTeamClicked() {
		setSeenWizard();
		_context.startActivity(new Intent(_context, CreateTeam.class));
	}
		
	private void skipWizardClicked() {
		setSeenWizard();
	}
	
	private void setSeenWizard() {
		SimpleSetting.SeenWizard.set(true);
	}
	
}
