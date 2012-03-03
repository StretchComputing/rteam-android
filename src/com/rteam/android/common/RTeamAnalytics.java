package com.rteam.android.common;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.rteam.api.base.ResponseStatus;
import com.rteam.api.business.Activity;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Member;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.business.NewMessageInfo;
import com.rteam.api.business.Team;

public class RTeamAnalytics {

	private GoogleAnalyticsTracker _tracker;
	private static final String UAAccountNumber = "UA-280128-5";
	private static final int DispatchIntervalSeconds = 10;
	
	private static final String UserActionCategory = "UserAction";
	
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
	
	public void trackActivityStart(android.app.Activity activity)
	{
		trackEvent(UserActionCategory, "StartActivity", activity.getClass().getName(), 1);
	}
	
	public void trackActivityResume(android.app.Activity activity)
	{
		trackEvent(UserActionCategory, "ResumeActivity", activity.getClass().getSimpleName(), 1);
	}
	
	public void trackActivityStop(android.app.Activity activity)
	{
		trackEvent(UserActionCategory, "StopActivity", activity.getClass().getSimpleName(), 1);
	}
	
	public void trackEventCreated(EventBase event)
	{
		trackEvent(UserActionCategory, "CreateEvent", event.eventType().toPrettyString(), 1);
	}
	
	public void trackTeamCreated(Team team)
	{
		trackEvent(UserActionCategory, "CreateTeam", team.sport().toString(), 1);
	}
	
	public void trackMessageCreated(NewMessageInfo message)
	{
		trackEvent(UserActionCategory, "CreateMessage", message.type().toString(), 1);
	}
	
	public void trackActivityCreated(Activity activity)
	{
		trackEvent(UserActionCategory, "CreateActivity", activity.photo() != null ? "Photo Message" : "Text Message", 1);
	}
	
	public void trackMemberCreated(Member member)
	{
		trackEvent(UserActionCategory, "CreateMember", member.participantRole().toString(), 1);
	}
	
	public void trackMemberUpdated(Member member)
	{
		trackEvent(UserActionCategory, "UpdateMember", member.participantRole().toString(), 1);
	}
	
	public void trackMessageResponse(MessageInfo message)
	{
		trackEvent(UserActionCategory, "MessageResponse", message.type().toString(), 1);
	}
	
	public void trackLogin(ResponseStatus apiResponse)
	{
		trackEvent(UserActionCategory, "Login", apiResponse.getErrorMessage(), 1);
	}
	
	public void trackRegister(ResponseStatus apiResponse)
	{
		trackEvent(UserActionCategory, "Register", apiResponse.getErrorMessage(), 1);
	}
	
	public void trackResetPassword(ResponseStatus apiResponse)
	{
		trackEvent(UserActionCategory, "ResetPassword", apiResponse.getErrorMessage(), 1);
	}
	
	public void trackSetResetPassword(ResponseStatus apiResponse)
	{
		trackEvent(UserActionCategory, "SetResetPassword", apiResponse.getErrorMessage(), 1);
	}
	
	public void trackChangePassword(ResponseStatus apiResponse)
	{
		trackEvent(UserActionCategory, "ChangePassword", apiResponse.getErrorMessage(), 1);
	}
	
	public void trackFeedback()
	{
		trackEvent(UserActionCategory, "Feedback", "", 1);
	}
	
	public void trackRate()
	{
		trackEvent(UserActionCategory, "Rate", "", 1);
	}
	
	public void trackApplicationLaunched()
	{
		trackEvent(UserActionCategory, "Application Launched", "", 1);
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
