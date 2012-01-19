package com.rteam.android.events.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;

import com.rteam.api.GamesResource;
import com.rteam.api.PracticeResource;
import com.rteam.api.GamesResource.GetGamesResponse;
import com.rteam.api.PracticeResource.GetPracticesResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Team;
import com.rteam.api.business.EventBase.GetAllEventBase;
import com.rteam.api.business.EventBase.GetAllForTeamEventBase;

public class EventLoader {

	public interface EventLoaderCallback {
		public void loading(boolean isLoading, String message);
		public void done(List<EventBase> eventsLoaded);
	}
	
	private Activity _context;
	private Team _team;
	private EventLoaderCallback _callback;
	private List<EventBase> _events;
	
	public EventLoader(Activity context, EventLoaderCallback callback) {
		this(context, null, callback);
	}
	
	public EventLoader(Activity context, Team team, EventLoaderCallback callback) {
		_context = context;
		_team = team;
		_callback = callback;
		
		_events = new ArrayList<EventBase>();
	}
	
	public void load() {
		_events.clear();
		loadGames();
	}
	
	private void loadGames() {
		_callback.loading(true, "Loading games...");
		if(_team != null) {
			GamesResource.instance().getForTeam(new GetAllForTeamEventBase(_team, Event.Type.Game), new GamesResource.GetGamesResponseHandler() {
				@Override public void finish(GetGamesResponse response) { loadGamesFinished(response); }
			});
		}
		else { 
			GamesResource.instance().getAll(new GetAllEventBase(Event.Type.Game), new GamesResource.GetGamesResponseHandler() {
				@Override public void finish(GetGamesResponse response) { loadGamesFinished(response); }
			});
		}
	}

	
	private void loadGamesFinished(GetGamesResponse response) {
		if(response.showError(_context)) {
			_events.addAll(response.games());
			loadPractices();
		}
		else {
			_callback.loading(false, null);
		}
	}
	
	private void loadPractices() {
		_callback.loading(true, "Loading practices...");
		if(_team != null) {
			PracticeResource.instance().getForTeam(new GetAllForTeamEventBase(_team, Event.Type.Practice), new PracticeResource.GetPracticesResponseHandler() {
				@Override public void finish(GetPracticesResponse response) { loadPracticesFinished(response); }
			});
		}
		else {
			PracticeResource.instance().getAll(new GetAllEventBase(Event.Type.Practice), false, new PracticeResource.GetPracticesResponseHandler() {
				@Override public void finish(GetPracticesResponse response) { loadPracticesFinished(response); }
			});
		}
	}
	
	private void loadPracticesFinished(GetPracticesResponse response) {
		if(response.showError(_context)) {
			_events.addAll(response.practices());
			loadGenericEvents();
		}
		else {
			_callback.loading(false, null);
		}
	}
	
	private void loadGenericEvents() {
		_callback.loading(true, "Loading other events...");
		if(_team != null) {
			PracticeResource.instance().getForTeam(new GetAllForTeamEventBase(_team, Event.Type.Generic), new PracticeResource.GetPracticesResponseHandler() {
				@Override public void finish(GetPracticesResponse response) { loadGenericEventsFinished(response); }
			});	
		}
		else {
			PracticeResource.instance().getAll(new GetAllEventBase(Event.Type.Generic), false, new PracticeResource.GetPracticesResponseHandler() {
				@Override public void finish(GetPracticesResponse response) { loadGenericEventsFinished(response); }
			});
		}
	}
	
	private void loadGenericEventsFinished(GetPracticesResponse response) {
		_callback.loading(false, null);
		if(response.showError(_context)) {
			_events.addAll(response.practices());
			Collections.sort(_events);
			_callback.done(_events);
		}
	}
}
