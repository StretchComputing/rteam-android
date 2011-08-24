package com.rteam.android.teams;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.events.EventDetails;
import com.rteam.android.teams.common.AddEventDialog;
import com.rteam.android.teams.common.EventListAdapter;
import com.rteam.android.teams.common.EventListAdapter.EventCheckedHandler;
import com.rteam.android.teams.common.EventListAdapter.EventClickedHandler;
import com.rteam.api.GamesResource;
import com.rteam.api.GamesResource.DeleteGameResponse;
import com.rteam.api.GamesResource.GetGamesResponse;
import com.rteam.api.PracticeResource.DeletePracticeResponse;
import com.rteam.api.PracticeResource.GetPracticesResponse;
import com.rteam.api.PracticeResource;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Game;
import com.rteam.api.business.Member;
import com.rteam.api.business.Practice;
import com.rteam.api.business.Team;

public class Events extends RTeamActivityChildTab {
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - team events"; }
	
	private ListView _listEvents;
	private Button _btnAddEvent;
	private Button _btnDeleteSelected;
	private View _barBottom;
	
	private Team getTeam() { return TeamDetails.getTeam(); }
	
	private ArrayList<EventBase> _allEvents;
	private ArrayList<EventBase> _checkedEvents;
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Allows you to manage the events for the current team, including adding or deleting games and practices."));
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Initialization/View Setup
	
	@Override
	protected void initialize() {
		initializeView();
		loadEvents();
	}
	
	
	private void initializeView() {
		setContentView(R.layout.teams_events);
		
		_listEvents = (ListView) findViewById(R.id.listEvents);
		_btnAddEvent = (Button) findViewById(R.id.btnAdd);
		_btnDeleteSelected = (Button) findViewById(R.id.btnDelete);
		_barBottom = findViewById(R.id.barBottom);
		
		_btnAddEvent.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { addEventClicked(); }
		});
		_btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { deleteSelectedClicked(); }
		});
	}
	
	private void bindView() {
		boolean canEdit = getTeam().participantRole().atLeast(Member.Role.Coordinator);
		_barBottom.setVisibility(canEdit ? View.VISIBLE : View.GONE);
		_checkedEvents = new ArrayList<EventBase>();
		
		_listEvents.setAdapter(new EventListAdapter(this, canEdit, _allEvents, 
			new EventCheckedHandler() {
				@Override
				public void onCheck(int index, long id, boolean checked) { eventChecked(_allEvents.get(index), checked); }}, 
			new EventClickedHandler() {
				@Override
				public void onClick(int index, long id) { eventClicked(_allEvents.get(index)); }
			}));
		bindDeletedButton();
	}
	
	private void bindDeletedButton() {
		_btnDeleteSelected.setEnabled(_checkedEvents.size() > 0);
	}
		
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void eventChecked(EventBase event, boolean checked) {
		if (checked) _checkedEvents.add(event);
		else		 _checkedEvents.remove(event);
		
		bindDeletedButton();
	}
	
	private void eventClicked(EventBase event) {
		EventDetails.setup(event, getTeam());
		startActivity(new Intent(this, EventDetails.class));
	}
	
	
	private void addEventClicked() {
		new AddEventDialog(this, getTeam(), new AddEventDialog.AddClickedHandler() {
			@Override
			public void addClicked(EventBase event) { addEvent(event); }
		}).show();
	}
	
	private void addEvent(EventBase event) {
		_allEvents.add(event);
		bindView();
	}
	
	
	
	private void deleteSelectedClicked() {
		for(EventBase event : _checkedEvents) {
			
			if (event.isGame()) deleteGame((Game) event);
			else 			    deletePractice((Practice) event);
			
			_allEvents.remove(event);
		}
		
		_checkedEvents.clear();
		bindView();
	}
	
	private void deleteGame(Game event) {
		CustomTitle.setLoading(true, "Deleting game...");
		new GamesResource()
			.delete(event, new GamesResource.DeleteGameResponseHandler() {
				@Override public void finish(DeleteGameResponse response) {
					CustomTitle.setLoading(false);
					response.showError(Events.this);
				}
			});
	}
	
	private void deletePractice(Practice practice) {
		CustomTitle.setLoading(true, "Deleting practice...");
		new PracticeResource()
			.delete(practice, new PracticeResource.DeletePracticeResponseHandler() {
				@Override public void finish(DeletePracticeResponse response) {
					CustomTitle.setLoading(false);
					response.showError(Events.this);
				}
			});
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Data Loading
	
	private void loadEvents() {
		CustomTitle.setLoading(true, "Loading games...");
		new GamesResource()
			.getForTeam(new EventBase.GetAllForTeamEventBase(getTeam().teamId(), Event.Type.All), new GamesResource.GetGamesResponseHandler() {
				@Override public void finish(GetGamesResponse response) { loadGamesFinished(response); }
			});
	}
	private void loadGamesFinished(GetGamesResponse response) {
		if (response.showError(this)) {
			_allEvents = new ArrayList<EventBase>();
			_allEvents.addAll(response.games());
			
			CustomTitle.setLoading(true, "Loading practices...");
			new PracticeResource()
				.getForTeam(new EventBase.GetAllForTeamEventBase(getTeam().teamId(), Event.Type.All), new PracticeResource.GetPracticesResponseHandler() {
					@Override
					public void finish(GetPracticesResponse response) {
						loadPracticesFinished(response);
					}
				});
		}
		else {
			CustomTitle.setLoading(false);
		}
	}
	
	private void loadPracticesFinished(GetPracticesResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_allEvents.addAll(response.practices());
			
			Collections.sort(_allEvents);
			Collections.reverse(_allEvents);
			
			bindView();
		}
	}
}