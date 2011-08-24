package com.rteam.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.rteam.api.business.Game;
import com.rteam.api.business.Practice;
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
	private final int REFRESH_RATE = 18000000; // 5 minutes
	public static final int NOTIFICATION_ID = 12345;
	
	private ArrayList<Practice> _practices;
	private ArrayList<Game> _games;
	
	//////////////////////////////////////////////////////////////////////////////
	//// .ctor
	public EventService() {
		_timer = new Timer(true);
		_timer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {
				refreshEvents();
			}
		}, 0, REFRESH_RATE);
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
	    	new PracticeResource().getAll(new EventBase.GetAllEventBase(Event.Type.All), true, new PracticeResource.GetPracticesResponseHandler() {
				@Override public void finish(GetPracticesResponse response) { loadPracticesFinished(response); }
			});
		} catch (Exception e) {
			RTeamLog.i(SUFFIX, e.getMessage());
		}
    }
    
    private void loadPracticesFinished(GetPracticesResponse response) {
    	try {
	    	_practices = response.practices();
	    	new GamesResource().getAll(new EventBase.GetAllEventBase(Event.Type.All), new GamesResource.GetGamesResponseHandler() {			
				@Override public void finish(GetGamesResponse response) { loadGamesFinished(response); }
			});
    	} catch (Exception e) {
			RTeamLog.i(SUFFIX, e.getMessage());
		}
    }
    
    private void loadGamesFinished(GetGamesResponse response) {
    	try {
			_games = response.games();
	    	loadUpcomingEventsFinished();
    	} catch (Exception e) {
			RTeamLog.i(SUFFIX, e.getMessage());
		}
    }
    
    private void loadUpcomingEventsFinished() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.MINUTE, 15);
    	ArrayList<EventBase> notifyEvents = new ArrayList<EventBase>();
    	
    	for (Practice practice : _practices) {
    		if ((practice.isInProgress() || practice.isUpcomingToday() || practice.isUpcomingToday())
					&& practice.startDate().before(c.getTime())) {
    			notifyEvents.add(practice);
    		}
    	}
    	for (Game game : _games) {
    		if ((game.isInProgress() || game.isUpcomingToday() || game.isTomorrow())
    				&& game.startDate().before(c.getTime())) {
    			notifyEvents.add(game);
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
