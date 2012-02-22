package com.rteam.android.common;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.rteam.api.business.Activity;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Member;
import com.rteam.api.business.NewMessageInfo;
import com.rteam.api.business.Team;

public class RTeamAnalytics {

	private GoogleAnalyticsTracker _tracker;
	private static final String UAAccountNumber = "UA-397069-3";
	private static final int DispatchIntervalSeconds = 10;
	
	private static final String UserActionCategory = "UserAction";
	
	private static final String ViewActivityAction = "ViewActivity";
	private static final String CreateEventAction = "CreateEvent";
	private static final String CreateTeamAction = "CreateTeam";
	private static final String CreateMessageAction = "CreateMessage";
	private static final String CreateActivityAction = "CreateActivity";
	private static final String CreateMemberAction = "CreateMember";
	private static final String UpdateMemberAction = "UpdateMember";
	
	public RTeamAnalytics(Context context) {
		_tracker = GoogleAnalyticsTracker.getInstance();
		_tracker.startNewSession(UAAccountNumber, DispatchIntervalSeconds, context);
	}
	
	public void trackEvent(String category, String action, String label, int value)
	{
		if(_tracker != null)
		{
			RTeamLog.i("Tracking : %s, %s, %s, %d", category, action, label, value);
			_tracker.trackEvent(category, action, label, value);
		}
	}
	
	public void trackActivityView(android.app.Activity activity)
	{
		trackEvent(UserActionCategory, ViewActivityAction, activity.getClass().getName(), 1);
	}
	
	public void trackEventCreated(EventBase event)
	{
		trackEvent(UserActionCategory, CreateEventAction, String.format("Event Type: %s", event.eventType().toPrettyString()), 1);
	}
	
	public void trackTeamCreated(Team team)
	{
		trackEvent(UserActionCategory, CreateTeamAction, String.format("Team Type: %s", team.sport().toString()), 1);
	}
	
	public void trackMessageCreated(NewMessageInfo message)
	{
		trackEvent(UserActionCategory, CreateMessageAction, String.format("Message Type: %s", message.type()), 1);
	}
	
	public void trackActivityCreated(Activity activity)
	{
		trackEvent(UserActionCategory, CreateActivityAction, activity.photo() != null ? "Photo Message" : "Text Message", 1);
	}
	
	public void trackMemberCreated(Member member)
	{
		trackEvent(UserActionCategory, CreateMemberAction, member.participantRole().toString(), 1);
	}
	
	public void trackMemberUpdated(Member member)
	{
		trackEvent(UserActionCategory, UpdateMemberAction, member.participantRole().toString(), 1);
	}
	
	public void dispose()
	{
		if(_tracker != null)
		{
			_tracker.stopSession();
			_tracker = null;
		}
	}
}
