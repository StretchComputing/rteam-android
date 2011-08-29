package com.rteam.android.events;

import android.widget.TextView;

import com.rteam.android.common.RTeamActivityChildTab;

public class Activity extends RTeamActivityChildTab {
	
	@Override
	protected String getCustomTitle() { return "rTeam - event activity"; }

	@Override
	protected void initialize() {
		TextView text = new TextView(this);
		text.setText("Activity View");
		setContentView(text);
	}
}
