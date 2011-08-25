package com.rteam.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.rteam.android.common.RTeamLog;
import com.rteam.android.common.RTeamLog.TagSuffix;
import com.rteam.android.common.SimpleSetting;
import com.rteam.android.events.LaunchEventDetails;
import com.rteam.api.GamesResource;
import com.rteam.api.PracticeResource;
import com.rteam.api.GamesResource.GetGamesResponse;
import com.rteam.api.PracticeResource.GetPracticesResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.common.DateUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class EventService extends Service {
	
	/////////////////////////////////////////////////////////////////////////////
	///// Members
	
	private static final TagSuffix SUFFIX = new TagSuffix("service");
	
	private Timer _timer;
	private final int REFRESH_RATE = 18000000; 	// 5 minutes
	private final int DELAY_REFRESH = 30000;	// 30 seconds
	public static final int NOTIFICATION_ID = 12345;
	
	private Map<String, EventBase> _events;
		
	//////////////////////////////////////////////////////////////////////////////
	//// .ctor
	public EventService() {
		_timer = new Timer(true);
		_timer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {
				refreshEvents();
			}
		}, DELAY_REFRESH, REFRESH_RATE);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//// Refresh Events
	private void refreshEvents() {
		if (SimpleSetting.ShowAlerts.getBoolean(true)) {
			loadUpcomingEvents();
		}
	}
	
	
	private void loadUpcomingEvents() {
		try {
			_events = new HashMap<String, EventBase>();
			GetPracticesResponse practiceResponse = new PracticeResource().getAll(new EventBase.GetAllEventBase(Event.Type.All), false);
			_events.putAll(practiceResponse.eventsMap());
			GetGamesResponse gamesResponse = new GamesResource().getAll(new EventBase.GetAllEventBase(Event.Type.All));
			_events.putAll(gamesResponse.eventsMap());
			
			loadUpcomingEventsFinished();
		} catch (Exception e) {
			RTeamLog.i(SUFFIX, e.getMessage());
		}
    }
    
    private void loadUpcomingEventsFinished() {
    	if (_events == null) return;
    	
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.MINUTE, 15);
    	ArrayList<EventBase> notifyEvents = new ArrayList<EventBase>();
    	
    	for (EventBase event : _events.values()) {
    		if ((event.isInProgress() || event.isUpcomingToday() || event.isUpcomingToday())
					&& event.startDate().before(c.getTime())) {
    			notifyEvents.add(event);
    		}
    	}
    	
    	Collections.sort(notifyEvents); Collections.reverse(notifyEvents);
    	for (EventBase event : notifyEvents) {
    		notifyEvent(event);
    	}
    }
    
    private void notifyEvent(EventBase event) {
    	NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification notification = new Notification(event.isGame() ? R.drawable.home_quicklink_gametoday : R.drawable.home_quicklink_practicetoday,
    												getNotificationText(event),
    												System.currentTimeMillis());
    	Intent intent = new Intent(this, LaunchEventDetails.class);
    	intent.putExtra(LaunchEventDetails.EVENT_ID, event.eventId());
    	intent.putExtra(LaunchEventDetails.TEAM_ID, event.teamId());
    	intent.putExtra(LaunchEventDetails.IS_PRACTICE, !event.isGame());
    	notification.setLatestEventInfo(getApplicationContext(), 
    									getNotificationTitle(event), 
    									getNotificationText(event), 
    									PendingIntent.getActivity(this, 0, intent, 0));
    	
    	notifier.notify(event.eventId(), NOTIFICATION_ID, notification);
    }
    
    protected String getNotificationTitle(EventBase event) {
    	return String.format("Upcoming %s for %s", 
								event.isGame() ? "game" : "practice",
								event.teamName());
    }
    
    protected String getNotificationText(EventBase event) {
    	return String.format("%s %s%s for %s", 
    							event.isGame() ? "Game" : "Practice",
								event.isInProgress() ? "In Progress" : (event.isUpcomingToday() ? "Today" : "Tomorrow"),
								event.isInProgress() ? "" : "at " + DateUtils.toStringTime(event.startDate()),
								event.teamName());
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
