package com.rteam.android.user;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rteam.android.R;
import com.rteam.android.common.RTeamActivityChildTab;

public class Feedback extends RTeamActivityChildTab {

	@Override
	protected String getCustomTitle() { return "rTeam - feedback"; }
	
	private Button _btnSendFeedback;
	private Button _btnRate;
	
	@Override
	protected void initialize() {
		initializeView();
	}
	
	
	private void initializeView() {
		setContentView(R.layout.user_feedback);
		
		_btnSendFeedback = (Button) findViewById(R.id.btnFeedback);
		_btnRate = (Button) findViewById(R.id.btnRate);
		
		
		_btnSendFeedback.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { sendFeedback(); }
		});
		
		_btnRate.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { rate(); }
		});
	}
	
	
	private void sendFeedback() {
		_tracker.trackFeedback();
		try {
			Intent email = new Intent(android.content.Intent.ACTION_SEND);
			email.setType("plain/text");
			email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "feedback@rteam.com" });
			email.putExtra(android.content.Intent.EXTRA_SUBJECT, "rTeam android feedback");
			startActivity(email);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(this, "Unknown error trying to load the email client.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void rate() {
		_tracker.trackRate();
		try {
			Intent rate = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rteam.android"));	// TODO: Fix this url
			startActivity(rate);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(this, "Unknown error trying to load the market.", Toast.LENGTH_SHORT).show();
		}
	}
	
}
