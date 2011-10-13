package com.rteam.android.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasonkostempski.android.calendar.CalendarDayMarker;
import com.jasonkostempski.android.calendar.CalendarView;
import com.jasonkostempski.android.calendar.CalendarView.OnMonthChangedListener;
import com.jasonkostempski.android.calendar.CalendarView.OnSelectedDayChangedListener;
import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.events.common.EventListAdapter;
import com.rteam.android.teams.common.AddEventDialog;
import com.rteam.android.teams.common.TeamSelectDialog;
import com.rteam.api.GamesResource;
import com.rteam.api.TeamsResource;
import com.rteam.api.GamesResource.GetGamesResponse;
import com.rteam.api.PracticeResource;
import com.rteam.api.PracticeResource.GetPracticesResponse;
import com.rteam.api.TeamsResource.GetTeamResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.EventBase.GetAllEventBase;
import com.rteam.api.business.Team;
import com.rteam.api.common.DateUtils;

public class EventsCalendar extends RTeamActivity {
	
	////////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - event calendar"; }
	
	private CalendarView _calendarEvents;
	private ListView _listSelectedEvents;
	
	private ArrayList<EventBase> _events = new ArrayList<EventBase>();
	private ArrayList<EventBase> _selectedEvents = new ArrayList<EventBase>();
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows the upcoming events for the current months."));
	}
	
	////////////////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		loadEvents();
	}
	
	private void initializeView() {
		setContentView(R.layout.events_calendar);
		
		_calendarEvents = (CalendarView) findViewById(R.id.calendar_events);
		_listSelectedEvents = (ListView) findViewById(R.id.listEvents);
			
		_calendarEvents.setOnMonthChangedListener(new OnMonthChangedListener() {
			@Override public void onMonthChanged(CalendarView view) { bindCalendar(); } 
		});
		_calendarEvents.setOnSelectedDayChangedListener(new OnSelectedDayChangedListener() {
			@Override public boolean onSelectedDayChanged(CalendarView view) { 
				bindEventInfo();
				return false; // Want to handle the day stuff ourselves
			} 
		});
		
		_listSelectedEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapater, View view, int position, long id) {
				if(position == 0) createEventClicked();
				else			  eventClicked(_selectedEvents.get(position - 1));
			}
		});
	}
	
	private void bindCalendar() {
		ArrayList<EventBase> eventsInRange = new ArrayList<EventBase>();
		
		Date start = _calendarEvents.getVisibleStartDate().getTime();
		Date end = _calendarEvents.getVisibleEndDate().getTime();
		for(EventBase event : _events) {
			if (event.startDate().after(start) && event.startDate().before(end)) {
				eventsInRange.add(event);
			}
		}
		
		ArrayList<CalendarDayMarker> markers = new ArrayList<CalendarDayMarker>();
		for(EventBase event : eventsInRange) {
			markers.add(new CalendarDayMarker(event.startDate(), Color.GRAY));
		}
		
		_calendarEvents.setDaysWithEvents(markers.toArray(new CalendarDayMarker[markers.size()]));
	}
	
	private void bindEventInfo() {
		Date selected = _calendarEvents.getSelectedDay().getTime();
		
		_selectedEvents.clear();
		for(EventBase event : _events) {
			if (DateUtils.areSameDay(selected, event.startDate())) {
				_selectedEvents.add(event);
			}
		}
		
		_listSelectedEvents.setAdapter(new EventListAdapter(this, _selectedEvents));
	}
	
	////////////////////////////////////////////////////////////////////////////
	//// Data Loading
	
	private void loadEvents() {
		_events.clear();
		loadGames();
	}
	
	private void loadGames() {
		CustomTitle.setLoading(true, "Loading games...");
		GamesResource.instance().getAll(new GetAllEventBase(Event.Type.All), new GamesResource.GetGamesResponseHandler() {
			@Override public void finish(GetGamesResponse response) { loadGamesFinished(response); }
		});
	}
	
	private void loadGamesFinished(GetGamesResponse response) {
		if (response.showError(this)) {
			_events.addAll(response.games());
			loadPractices();
		}
		else {
			CustomTitle.setLoading(false);
		}
	}
	
	private void loadPractices() {
		CustomTitle.setLoading(true, "Loading practices...");
		PracticeResource.instance().getAll(new GetAllEventBase(Event.Type.All), true, new PracticeResource.GetPracticesResponseHandler() {
			@Override public void finish(GetPracticesResponse response) { loadPracticesFinished(response); }
		});
	}
	
	private void loadPracticesFinished(GetPracticesResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_events.addAll(response.practices());
			Collections.sort(_events);
			
			bindCalendar();	
			bindEventInfo();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void createEventClicked() {
		new TeamSelectDialog(this, new TeamSelectDialog.TeamSelectHandler() {
			@Override public void teamSelected(Team team) { createEventFor(team); } });
	}
	
	private void createEventFor(Team team) {
		new AddEventDialog(this, team, _calendarEvents.getSelectedDay().getTime(), new AddEventDialog.AddClickedHandler() {
			@Override public void addClicked(EventBase event) { addEvent(event); } }).show();
	}
		
	private void addEvent(EventBase event) {
		_events.add(event);
		Collections.sort(_events);
		
		bindCalendar();	
		bindEventInfo();
	}
	
	private void eventClicked(final EventBase event) {
		TeamsResource.instance().getTeam(event.teamId(), new TeamsResource.GetTeamResponseHandler() { 
			@Override public void finish(GetTeamResponse response) { if (response.showError(EventsCalendar.this)) eventClickedFinished(event, response.team()); }
		});
	}
	
	private void eventClickedFinished(EventBase event, Team team) {
		EventDetails.setup(event, team);
		startActivity(new Intent(this, EventDetails.class));
	}
}
