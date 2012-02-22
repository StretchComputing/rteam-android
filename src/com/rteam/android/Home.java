package com.rteam.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.common.QuickLink;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.common.RTeamLog;
import com.rteam.android.common.SimpleSetting;
import com.rteam.android.events.EventsCalendar;
import com.rteam.android.events.common.EventLoader;
import com.rteam.android.messaging.Messages;
import com.rteam.android.messaging.TwitterActivity;
import com.rteam.android.teams.CreateTeam;
import com.rteam.android.teams.MyTeams;
import com.rteam.android.teams.TeamDetails;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.android.teams.common.TeamSelectDialog;
import com.rteam.api.MessageThreadsResource;
import com.rteam.api.MessageThreadsResource.GetMessageCountResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Team;
import com.rteam.api.business.Member.Role;
import com.rteam.api.common.StringUtils;

public class Home extends RTeamActivity {
	
	//////////////////////////////////////////////////////////////////
	/// Members
	
	private Button _btnTeams;
	private Button _btnActivities;
	private Button _btnMessages;
	private Button _btnEvents;
	private Button _btnCreateTeam;
	private Button _btnMyTeam;
	
	private TextView _txtUnreadMessages;
	
	private TextView _quickLinksLabel;
	private LinearLayout _viewQuickLinks;
	private ProgressBar _quickLinksProgress;
	
	private String _numberUnreadMessages = null;
	
	private Map<String, EventBase> _events;
	
	private ArrayList<EventBase> _gamesInProgress;
	private ArrayList<EventBase> _eventsToday;
	private ArrayList<EventBase> _eventsTomorrow;

	//////////////////////////////////////////////////////////////////
	/// Initialize
	
	@Override
    public void onBackPressed() {
    	moveTaskToBack(true);
    }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "This is the rTeam home screen.  It provides quick access to most of the major features of rTeam."),
								new HelpContent("Teams", "Opens a view showing all of the teams that you are currently enrolled in or following."),
								new HelpContent("Activity", "Shows you an overview of the current activity of all of your teams."),
								new HelpContent("Messages", "Shows you a list of the messages that you have sent and recieved."),
								new HelpContent("Events", "Provides an overview of the events for the current month and upcoming months."),
								new HelpContent("Create Team", "If you are a coach or team manager and want to manage your team, you can create a new team using this option."),
								new HelpContent("Quick Links", "Shows upcoming events for the teams that you are currently enrolled in or following."));
	}
	
	@Override
	protected String getCustomTitle() { return "rTeam - home"; }
	
	@Override
	protected boolean showHomeButton() { return false; }
		
    @Override
    protected void initialize() {
    	TeamCache.initialize(new TeamCache.DoneLoadingCallback() {
			@Override
			public void doneLoading() {
				bindHomeTeam();
			}
		});
    	
    	RTeamLog.i("Simple Settings: Auto Login: %s", SimpleSetting.AutoLogin.get());
    	
    	initializeView();
    	loadMessages();
    	loadUpcomingEvents();
    	checkForWizard();
    }
    
    @Override
    protected void reInitialize() {    	
    	// make sure the view is initialized...
    	if(_txtUnreadMessages == null) {
    		initializeView();
    	}
    	
    	bindUnreadMessages();
    	bindHomeTeam();
    }
    
    private void initializeView() {
    	setContentView(R.layout.home);
    	
    	_txtUnreadMessages = (TextView) findViewById(R.id.txtUnreadMessages);
    	
    	_btnTeams = (Button) findViewById(R.id.btnTeams);
    	_btnActivities = (Button) findViewById(R.id.btnActivities);
    	_btnMessages = (Button) findViewById(R.id.btnMessages);
    	_btnEvents = (Button) findViewById(R.id.btnEvents);
    	_btnCreateTeam = (Button) findViewById(R.id.btnCreateTeam);
    	_btnMyTeam = (Button) findViewById(R.id.btnMyTeam);
    	
    	_quickLinksLabel = (TextView) findViewById(R.id.lblQuicklinks);
    	_viewQuickLinks = (LinearLayout) findViewById(R.id.viewQuickLinks);
    	_quickLinksProgress = (ProgressBar) findViewById(R.id.progressQuickLinks);
    	
    	_btnTeams.setOnClickListener(new View.OnClickListener() 		{ @Override public void onClick(View v) { teamsClicked(); } });
    	_btnActivities.setOnClickListener(new View.OnClickListener() 	{ @Override public void onClick(View v) { activitiesClicked(); } });
    	_btnMessages.setOnClickListener(new View.OnClickListener() 		{ @Override public void onClick(View v) { messagesClicked(); } });
    	_btnEvents.setOnClickListener(new View.OnClickListener() 		{ @Override public void onClick(View v) { eventsClicked(); } });
    	_btnCreateTeam.setOnClickListener(new View.OnClickListener() 	{ @Override public void onClick(View v) { createTeamClicked(); } });
    	_btnMyTeam.setOnClickListener(new View.OnClickListener() 		{ @Override public void onClick(View v) { myTeamClicked(); } });
    	
    	bindUnreadMessages();
    	bindHomeTeam();
    }
    
    private boolean hasHomeTeamSet() {
    	return SimpleSetting.MyTeam.exists();
    }
    
    private Team getHomeTeam() {
    	if(hasHomeTeamSet()) {
    		return TeamCache.get(SimpleSetting.MyTeam.get());
    	}
    	
    	return null;
    }
    
    private void bindUnreadMessages() {
    	_txtUnreadMessages.setVisibility(_numberUnreadMessages != null && !_numberUnreadMessages.equalsIgnoreCase("0") ? View.VISIBLE : View.INVISIBLE);
    	_txtUnreadMessages.setText(_numberUnreadMessages);
    }
    
    private void bindHomeTeam() {
    	String myTeamText = "Loading..."; 
    	if(hasHomeTeamSet()) {
    		if (TeamCache.isInitialized()) {
    			Team homeTeam = getHomeTeam();
    			myTeamText = homeTeam != null ? StringUtils.truncate(homeTeam.teamName(), 12) : "Unknown";
        	}
    	}
    	else if(TeamCache.isInitialized()) {
    		myTeamText = "Create Team";
    		if(TeamCache.getTeamsCount() > 0) {
    			myTeamText = "Select team...";    			
    		}
    	}

    	_btnMyTeam.setText(myTeamText);
    }
    
    private void bindQuickLinks() {    	
    	_viewQuickLinks.removeAllViewsInLayout();
    	if (_gamesInProgress.size() == 0 && _eventsToday.size() == 0 && _eventsTomorrow.size() == 0)
    	{
    		if(hasHomeTeamSet() && getHomeTeam().participantRole().atLeast(Role.Coordinator)) {    		
	    		_viewQuickLinks.addView(new QuickLink.QuickLinkCreateEvent(this, _tracker, false, getHomeTeam(), new QuickLink.QuickLinkCreateEvent.RefreshEventsHandler() {
					@Override public void refreshEvents() { loadUpcomingEvents(); } 
				}).getView());
	    		_viewQuickLinks.addView(new QuickLink.QuickLinkCreateEvent(this, _tracker, true, getHomeTeam(), new QuickLink.QuickLinkCreateEvent.RefreshEventsHandler() {
					@Override public void refreshEvents() { loadUpcomingEvents(); } 
				}).getView());
    		}
    		else {
    			_quickLinksLabel.setVisibility(View.GONE);
    		}
    	}
    	else
    	{
	    	for (EventBase game : _gamesInProgress) {
	    		_viewQuickLinks.addView(new QuickLink.QuickLinkShowEvent(this, game).getView());
	    	}
	    	for (EventBase event : _eventsToday) {
	    		_viewQuickLinks.addView(new QuickLink.QuickLinkShowEvent(this, event).getView());
	    	}
	    	for (EventBase event : _eventsTomorrow) {
	    		_viewQuickLinks.addView(new QuickLink.QuickLinkShowEvent(this, event).getView());
	    	}
    	}
    }
    
    private void loadMessages() {
    	CustomTitle.setLoading(true);
    	MessageThreadsResource.instance().getMessageCount(new MessageThreadsResource.GetMessageCountResponseHandler() {
			@Override
			public void finish(GetMessageCountResponse response) {
				if (response.showError(Home.this)) {
					_numberUnreadMessages = Integer.toString(response.messageCount());
					bindUnreadMessages();
				}
			}
		});
    }
    
    private void loadUpcomingEvents() {
    	_quickLinksProgress.setVisibility(View.VISIBLE);
    	_events = new HashMap<String, EventBase>();
    	EventLoader loader = new EventLoader(this, new EventLoader.EventLoaderCallback() {
			@Override
			public void loading(boolean isLoading, String message) {
				_quickLinksProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
			}
			
			@Override
			public void done(List<EventBase> eventsLoaded) {
				for(EventBase event : eventsLoaded) {
					if(!_events.containsKey(event.eventId())) {
						_events.put(event.eventId(), event);
					}
				}
				
				loadUpcomingEventsFinished();
			}
		});
    	
    	// begin loading
    	loader.load();
    }
        
    private void loadUpcomingEventsFinished() {
    	_gamesInProgress = new ArrayList<EventBase>();
    	_eventsToday = new ArrayList<EventBase>();
    	_eventsTomorrow = new ArrayList<EventBase>();
    	
    	if (_events != null) {
	    	for (EventBase evt : _events.values()) {
	    		if (evt.isInProgress() && evt.eventType() == Event.Type.Game) {
	    			_gamesInProgress.add(evt);
	    		}
	    		else if (evt.isUpcomingToday()) {
	    			_eventsToday.add(evt);
	    		}
	    		else if (evt.isTomorrow()) {
	    			_eventsTomorrow.add(evt);
	    		}
	    	}
    	}
    	
    	Collections.sort(_gamesInProgress); Collections.reverse(_gamesInProgress);
    	Collections.sort(_eventsToday); 	Collections.reverse(_eventsToday);
    	Collections.sort(_eventsTomorrow); 	Collections.reverse(_eventsTomorrow);
    	
    	bindQuickLinks();
    }
    
    private void checkForWizard() {
    	if (!SimpleSetting.SeenWizard.getBoolean()) {
    		if (TeamCache.getTeamsCount() == 0) {
    			new NewUserDialog(this).showDialog();
    		}
    		else {
    			SimpleSetting.SeenWizard.set(true);
    		}
    	}
    }
    
	//////////////////////////////////////////////////////////////////
	/// Event Handlers
    
    private void teamsClicked() { 
    	startActivity(new Intent(this, MyTeams.class)); 
    }
    
    private void activitiesClicked() {
    	TwitterActivity.clear();
    	startActivity(new Intent(this, TwitterActivity.class)); 
	}
    
    private void messagesClicked() { 
    	startActivity(new Intent(this, Messages.class)); 
	}
    
    private void createTeamClicked() { 
    	startActivity(new Intent(this, CreateTeam.class)); 
	}
    
    private void eventsClicked() { 
    	startActivity(new Intent(this, EventsCalendar.class)); 
	}
    
    private void myTeamClicked() {
    	if (hasHomeTeamSet()) {
    		TeamDetails.setTeam(getHomeTeam());
    		startActivity(new Intent(this, TeamDetails.class));
    	}
    	else if(TeamCache.isInitialized() && TeamCache.getTeamsCount() > 0) {
    		new TeamSelectDialog(this, "Select Home Team", new TeamSelectDialog.TeamSelectHandler() {
				@Override public void teamSelected(Team team) {
					if(team != null) {
						SimpleSetting.MyTeam.set(team.teamId());
						bindHomeTeam();
					}
				}
			});
    	}
    	else {
    		createTeamClicked();
    	}
    }
}