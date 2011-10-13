package com.rteam.android.teams;

import java.util.List;

import android.widget.ListView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.teams.common.AllScoresListActivity;
import com.rteam.api.GamesResource;
import com.rteam.api.GamesResource.GetGamesResponse;
import com.rteam.api.GamesResource.GetGamesResponseHandler;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase.GetAllForTeamEventBase;
import com.rteam.api.business.Game;

public class AllScores extends RTeamActivity {
	
	//////////////////////////////////////////////////////////////
	//// Members
	
	private List<Game> _games;
	private ListView _listScores;

	@Override protected String getCustomTitle() { return String.format("rTeam - all scores for %s", TeamDetails.getTeam().teamName());	}


	//////////////////////////////////////////////////////////////
	//// Initialization

	@Override
	protected void initialize() {
		initializeView();
		loadData();
	}
	
	private void initializeView() {
		setContentView(R.layout.teams_allscores);
		
		_listScores = (ListView) findViewById(R.id.listScores);
	}
	
	private void bindView() {
		for (Game game : _games) {
			game.bindTeam(TeamDetails.getTeam());
		}
		_listScores.setAdapter(new AllScoresListActivity(this, _games));
	}
	
	private void loadData() {
		CustomTitle.setLoading(true, "Loading scores...");
		GamesResource.instance().getForTeam(new GetAllForTeamEventBase(TeamDetails.getTeam().teamId(), Event.Type.Game), new GetGamesResponseHandler() {
			@Override 
			public void finish(GetGamesResponse response) {
				CustomTitle.setLoading(false);
				if (response.showError(AllScores.this)) {
					_games = response.games();
					bindView();
				}
			}
		});
	}
}
