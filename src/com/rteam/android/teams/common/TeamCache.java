package com.rteam.android.teams.common;

import java.util.HashMap;
import java.util.Map;

import com.rteam.api.TeamsResource;
import com.rteam.api.TeamsResource.GetTeamResponse;
import com.rteam.api.TeamsResource.TeamListResponse;
import com.rteam.api.business.Team;

public class TeamCache {
	
	public static interface DoneLoadingCallback {
		public void doneLoading();
	}
	
	private static Map<String, Team> _teams = new HashMap<String, Team>();
	private static boolean _initialized = false;
	
	public static Team get(String teamId) {
		if (!_teams.containsKey(teamId)) {
			GetTeamResponse response = new TeamsResource().getTeam(teamId);
			if (response.checkResponse() && response.team() != null) {
				put(response.team());
			}
			else {
				_teams.put(teamId, null);
			}
		}
		return _teams.get(teamId);
	}
	
	
	public static int getTeamsCount() {
		return _teams.size();
	}
	
	public static void initialize(final DoneLoadingCallback callback) {
		if (_initialized) return;
		new TeamsResource().getTeams(new TeamsResource.TeamListResponseHandler() {
			@Override
			public void finish(TeamListResponse response) {
				_initialized = true;
				if (response.checkResponse()) {
					for (Team team : response.teams()) {
						put(team);
					}
				}
				
				callback.doneLoading();
			}
		});
	}
	
	public static void put(Team team) {
		_teams.put(team.teamId(), team);
	}
}
