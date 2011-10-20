package com.rteam.android.teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.SimpleMenuItem;
import com.rteam.android.common.SimpleSetting;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.events.EventDetails;
import com.rteam.api.GamesResource;
import com.rteam.api.GamesResource.GetGamesResponse;
import com.rteam.api.PracticeResource;
import com.rteam.api.PracticeResource.GetPracticesResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Game;
import com.rteam.api.business.Practice;
import com.rteam.api.business.Team;
import com.rteam.api.common.DateUtils;

public class TeamHome extends RTeamActivityChildTab {
	
	////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - team home"; }
	
	private TextView _txtTeamName;
	private ListView _listRecentGames;
	private TextView _listRecentGamesEmpty;
	private TextView _txtNextGame;
	private TextView _txtNextEvent;
	private TextView _txtAllScores;
		
	private Team team() { return TeamDetails.getTeam(); }
	
	private ArrayList<Game> _allTeamGames;
	private ArrayList<Practice> _allTeamEvents;
	
	private ArrayList<Game> _recentGames = new ArrayList<Game>();
	private Game _nextGame;
	private Practice _nextEvent;
	
	private boolean hasNextGame() { return _nextGame != null; }
	private boolean hasNextEvent() { return _nextEvent != null; }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows you the recent and upcoming games/events for your current team."));
	}
	
	@Override
	protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() {
		ArrayList<SimpleMenuItem> arr = new ArrayList<SimpleMenuItem>();
		
		arr.add(new SimpleMenuItem("Set as Home Team", new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				SimpleSetting.MyTeam.set(team().teamId());
				return false;
			}
		}));
		
		return arr; 
	}
	
	////////////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		if (team() == null) {
			finish();
			startActivity(new Intent(this, Home.class));
			Toast.makeText(this, "Unable to find team", Toast.LENGTH_SHORT).show();
		}
		initializeView();
		loadData();
	}
	
	private void initializeView() {
		// Set layout
		setContentView(R.layout.teams_teamhome);
		
		_txtTeamName = (TextView) findViewById(R.id.txtTeamName);
		_listRecentGames = (ListView) findViewById(R.id.listRecentGames);
		_listRecentGamesEmpty = (TextView) findViewById(R.id.listRecentGamesEmpty);
		_txtNextGame = (TextView) findViewById(R.id.txtNextGame);
		_txtNextEvent = (TextView) findViewById(R.id.txtNextEvent);
		_txtAllScores = (TextView) findViewById(R.id.txtAllScoresLink);
				
		// Set listeners
		_listRecentGames.setOnItemClickListener(new OnItemClickListener() {
			@Override 
			public void onItemClick(AdapterView<?> a, View v, int i, long l) { recentGameClicked(i); }			
		});
		
		_txtNextGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { nextGameClicked(); }
		});
		
		_txtNextEvent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { nextEventClicked(); }
		});
		
		_txtAllScores.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { allScoresClicked(); }
		});
	}
	
	private void bindView() {
		_txtTeamName.setText(team().teamName());
		
		ArrayList<String> games = new ArrayList<String>();
		for(Game g : _recentGames) {
			games.add(DateUtils.toPrettyString(g.startDate()));
		}
		_listRecentGames.setAdapter(new ArrayAdapter<String>(this, R.layout.list_simple, R.id.txtString, games.toArray(new String[games.size()])));
		_listRecentGames.setEmptyView(_listRecentGamesEmpty);
				
		_txtNextGame.setText(hasNextGame() ? DateUtils.toPrettyString2(_nextGame.startDate()) : "No games scheduled.");
		_txtNextEvent.setText(hasNextEvent() ? DateUtils.toPrettyString2(_nextEvent.startDate()) : "None scheduled.");
	}
	
	
	//////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void recentGameClicked(int index) {
		startEventActivity(_recentGames.get(index));
	}
	
	private void nextGameClicked() {
		if (hasNextGame()) {
			startEventActivity(_nextGame);
		}
	}
	
	private void nextEventClicked() {
		if (hasNextEvent()) {
			startEventActivity(_nextEvent);
		}
	}
	
	private void allScoresClicked() {
		startActivity(new Intent(this, AllScores.class));
	}
	
	private void startEventActivity(EventBase event) {
		EventDetails.setup(event, TeamDetails.getTeam());
		startActivity(new Intent(this, EventDetails.class));
	}
	
	//////////////////////////////////////////////////////////////////////
	//// Loading games
	
	private void loadData() {
		CustomTitle.setLoading(true, "Loading games...");
		GamesResource.instance()
			.getForTeam(new EventBase.GetAllForTeamEventBase(team(), Event.Type.All), new GamesResource.GetGamesResponseHandler() {
				@Override
				public void finish(GetGamesResponse response) { loadGamesFinished(response); }
			});
	}
	
	private void loadGamesFinished(GetGamesResponse response) {
		if (response.showError(this)) {
			_allTeamGames = response.games();
			
			CustomTitle.setLoading(true, "Loading practices...");
			PracticeResource.instance()
				.getForTeam(new EventBase.GetAllForTeamEventBase(team(), Event.Type.All), new PracticeResource.GetPracticesResponseHandler() {
					@Override
					public void finish(GetPracticesResponse response) { loadPracticesFinished(response); }
				});
		}
		else {
			CustomTitle.setLoading(false);
		}
	}
	
	private void loadPracticesFinished(GetPracticesResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_allTeamEvents = response.practices();
			loadDataFinish();
		}
	}
	
	private void loadDataFinish() {
		// Sort and reverse games and practices
		Collections.sort(_allTeamGames);
		Collections.reverse(_allTeamGames);
		
		Collections.sort(_allTeamEvents);
		Collections.reverse(_allTeamEvents);
		
		// Find the recent games
		Date today = new Date();
		_recentGames.clear();
		for(Game g : _allTeamGames) {
			if (g.startDate().before(today)) {
				_recentGames.add(g);
			}
		}
		
		// Find the next game/event
		_nextGame = null;
		for(Game g : _allTeamGames) {
			if (g.startDate().after(today)) {
				_nextGame = g;
				break;
			}
		}
		
		_nextEvent = null;
		for (Practice p : _allTeamEvents) {
			if (p.startDate().after(today)) {
				_nextEvent = p;
				break;
			}
		}
		
		bindView();
	}
}
