package com.rteam.android.events;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rteam.android.EventService;
import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.GamesResource;
import com.rteam.api.PracticeResource;
import com.rteam.api.GamesResource.GetGameResponse;
import com.rteam.api.PracticeResource.GetPracticeResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.EventBase.GetEventBase;

public class LaunchEventDetails extends RTeamActivity {

	public static final String TEAM_ID = "TeamID";
	public static final String EVENT_ID = "EventID";
	public static final String IS_PRACTICE = "IsPractice";
	
	private String _teamId;
	private String _eventId;
	private boolean _isPractice;
	
	@Override
	protected String getCustomTitle() { return "rTeam - launching event details"; }
	
	@Override
	protected void initialize() {	
		setContentView(R.layout.events_loading);		
		getExtras();
		
		if (_teamId == null || _eventId == null) {
			goHome();
			return;
		}
		
		cancelNotification();
		
		if (_isPractice) {
			loadPractice();
		}
		else {
			loadGame();
		}
	}
	
	private void cancelNotification() {
		NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notifier.cancel(_eventId, EventService.NOTIFICATION_ID);
	}
	
	
	private void getExtras() {
		if (getIntent().getExtras().containsKey(TEAM_ID)) _teamId = getIntent().getExtras().getString(TEAM_ID);
		if (getIntent().getExtras().containsKey(EVENT_ID)) _eventId = getIntent().getExtras().getString(EVENT_ID);
		if (getIntent().getExtras().containsKey(IS_PRACTICE)) _isPractice = getIntent().getExtras().getBoolean(IS_PRACTICE);
	}
	
	private void loadPractice() {
		CustomTitle.setLoading(true);
		new PracticeResource().get(new GetEventBase(_eventId, _teamId, Event.Type.Practice), new PracticeResource.GetPracticeResponseHandler() {
			@Override
			public void finish(GetPracticeResponse response) {
				CustomTitle.setLoading(false);
				if (response.checkResponse()) {
					launchEventDetails(response.practice());
				}
				else {
					notifyFailed();
				}
			} 
		});
	}
	
	private void loadGame() {
		CustomTitle.setLoading(true);
		new GamesResource().get(new GetEventBase(_eventId, _teamId, Event.Type.All), new GamesResource.GetGameResponseHandler() {
			@Override
			public void finish(GetGameResponse response) {
				CustomTitle.setLoading(false);
				if (response.checkResponse()) {
					launchEventDetails(response.game());
				}
				else {
					notifyFailed();
				}
			}
		});
	}
	
	
	
	private void launchEventDetails(EventBase event) {
		EventDetails.setup(event, TeamCache.get(_teamId));
		finish();
		startActivity(new Intent(this, EventDetails.class));
	}
	
	private void notifyFailed() {
		Toast.makeText(this, "Unable to find the event.", Toast.LENGTH_SHORT).show();
		goHome();
	}
	
	private void goHome() {
		finish();
		startActivity(new Intent(this, Home.class));
	}
}
