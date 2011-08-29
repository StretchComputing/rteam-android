package com.rteam.android.common;

import java.util.Date;

import com.rteam.android.R;
import com.rteam.android.events.EventDetails;
import com.rteam.android.teams.common.AddEventDialog;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.android.teams.common.TeamSelectDialog;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Team;
import com.rteam.api.common.DateUtils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class QuickLink {
	
	
	//////////////////////////////////////////////////////////////////
	//// Implementation Classes
	public static class QuickLinkShowEvent extends QuickLink {

		////////////////////////////////////////////////////////////////
		//// Members
		private EventBase _event;
		
		////////////////////////////////////////////////////////////////
		//// .ctor
		public QuickLinkShowEvent(Context context, EventBase event) {
			super(context);
			_event = event;
		}
		
		///////////////////////////////////////////////////////////////
		//// Implementation
		@Override
		protected int getImageResourceId() {
			int resId = -1;
	    	if (_event.isInProgress()) 		resId = R.drawable.home_quicklink_gameinprogress;
	    	if (_event.isUpcomingToday())	resId = _event.isGame() ? R.drawable.home_quicklink_gametoday 	 : R.drawable.home_quicklink_practicetoday;
	    	if (_event.isTomorrow())		resId = _event.isGame() ? R.drawable.home_quicklink_gametomorrow : R.drawable.home_quicklink_practicetomorrow;
	    	return resId;
		}
		
		@Override
		protected String getEventString() {
			return String.format("%s %s%s\n(%s)", 
									_event.isGame() ? "Game" : "Practice", 
									_event.isInProgress() ? "In Progress" : (_event.isUpcomingToday() ? "Today" : "Tomorrow"),
									_event.isInProgress() ? "" : ", " + DateUtils.toStringTime(_event.startDate()),
									_event.teamName());	
		}

		@Override
		protected void clickQuickLink() {
			EventDetails.setup(_event, TeamCache.get(_event.teamId()));
			_context.startActivity(new Intent(_context, EventDetails.class));
		}
	}
	
	public static class QuickLinkCreateEvent extends QuickLink {
		//////////////////////////////////////////////////////////////////
		//// Members
		private boolean _practice;
		private Team _defaultTeam;
		
		private RefreshEventsHandler _refresh;
		
		public interface RefreshEventsHandler {
			public void refreshEvents();
		}
		
		//////////////////////////////////////////////////////////////////
		//// .ctor
		public QuickLinkCreateEvent(Context context, boolean practice, Team defaultTeam, RefreshEventsHandler refresh) {
			super(context);
			_defaultTeam = defaultTeam;
			_practice = practice;
			_refresh = refresh;
		}
		
		///////////////////////////////////////////////////////////////
		//// Implementation
		@Override
		protected int getImageResourceId() {
			return _practice ? R.drawable.home_quicklink_practicetomorrow : R.drawable.home_quicklink_gametomorrow; 
		}
		
		@Override
		protected String getEventString() {
			return String.format("create %s%s%s", _practice ? "practice" : "game",
												  _defaultTeam != null ? "\nfor " : "",
												  _defaultTeam != null ? _defaultTeam.teamName() : "");
		}

		@Override
		protected void clickQuickLink() {
			if (_defaultTeam == null) {
				new TeamSelectDialog(_context, new TeamSelectDialog.TeamSelectHandler() {
					@Override public void teamSelected(Team team) { launchCreateEventFor(team); }
				});
			}
			else {
				launchCreateEventFor(_defaultTeam);
			}
		}
		
		private void launchCreateEventFor(Team team) {
			new AddEventDialog(_context, team, new Date(), _practice ? Event.Type.Practice : Event.Type.Game, new AddEventDialog.AddClickedHandler() {
				@Override public void addClicked(EventBase event) { 
					Toast.makeText(_context, "Event Created", Toast.LENGTH_SHORT).show();
					_refresh.refreshEvents();
			} }).show();
		}
	}
	
	/////////////////////////////////////////////////////////////////
	//// Members
	private LayoutInflater _layoutInflater;
	protected Context _context;
	
	private View _view;
	
	private ImageView _imageMain;
	private TextView _txtEvent;
	
	/////////////////////////////////////////////////////////////////
	///// .ctor

	protected QuickLink(Context context) {
		_context = context;
		_layoutInflater = LayoutInflater.from(context);		
	}
	
	////////////////////////////////////////////////////////////////
	//// Helpers
	public View getView() {
		if (_view == null) {
			initializeView();
		}
		return _view;
	}
	
	private void initializeView() {
		_view = _layoutInflater.inflate(R.layout.home_quicklink, null);
		
		_imageMain = (ImageView) _view.findViewById(R.id.imageMain);
		_txtEvent = (TextView) _view.findViewById(R.id.txtEvent);
		
		bindView();
	}
	
	private void bindView() {
		_imageMain.setImageResource(getImageResourceId());
		_imageMain.setOnClickListener(new View.OnClickListener() {
    		@Override public void onClick(View v) { clickQuickLink(); }
    	});
		_txtEvent.setText(getEventString());
	}
	 
	///////////////////////////////////////////////////////////////////////////
	///// To Implement
	
	protected abstract int getImageResourceId();
	protected abstract String getEventString();
	protected abstract void clickQuickLink();
}

