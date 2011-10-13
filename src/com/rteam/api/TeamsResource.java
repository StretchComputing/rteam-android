package com.rteam.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.RTeamApplicationVersion;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.Team;
import com.rteam.api.common.UriBuilder;

public class TeamsResource extends ResourceBase {
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// .ctor
	
	public static TeamsResource instance() {
		if (_instance == null) _instance = new TeamsResource();
		return _instance;
	}
	
	private static TeamsResource _instance;
	
	private TeamsResource() {
		super(AndroidTokenStorage.get(), RTeamApplicationVersion.get());
	}
		
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Response Classes
	
	public class TeamListResponse extends ResourceResponse
	{
		private ArrayList<Team> _teams;
		public ArrayList<Team> teams() { return _teams; }
		
		public TeamListResponse(APIResponse response) {
			super(response);
			
			_teams = new ArrayList<Team>();
			
			initialize();
		}	
		
		private void initialize() {
			if (isResponseGood()) {
				try {
					JSONArray teams = json().getJSONArray("teams");
					for(int i=0; i<teams.length(); i++) {
						_teams.add(new Team(teams.getJSONObject(i)));
					}
				} catch (JSONException e) {}
			}
		}
	}
	public interface TeamListResponseHandler {
		public void finish(TeamListResponse response);
	}
	
	public class CreateTeamResponse extends ResourceResponse {
		public String getTwitterAuthorizationUrl() {
			return _twitterAuthorizationUrl;
		}
		private String _twitterAuthorizationUrl;
		
		protected CreateTeamResponse(APIResponse response, Team team) {
			super(response);
			initialize(team);
		}
		
		private void initialize(Team team) {
			if (isResponseGood()) {
				team.teamId(json().optString("teamId"));
				team.teamPageUrl(json().optString("teamPageUrl"));
				_twitterAuthorizationUrl = json().optString("twitterAuthorizationUrl");
			}
		}
	}
	public interface CreateTeamResponseHandler {
		public void finish(CreateTeamResponse response);
	}
	
	public class GetTeamResponse extends ResourceResponse {
		private Team _team;
		public Team team() { return _team; }

		protected GetTeamResponse(APIResponse response) {
			super(response);
			initialize();
		}

		private void initialize() {
			if (isResponseGood()) {
				_team = new Team(json());
			}
		}
	}
	public interface GetTeamResponseHandler {
		public void finish(GetTeamResponse response);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Exposed Methods
	
	public TeamListResponse getTeams() {
		return new TeamListResponse(get(createBuilder().addPath("teams")));
	}
	
	public void getTeams(final TeamListResponseHandler handler) {
		(new AsyncTask<Void, Void, TeamListResponse>() {

			@Override
			protected TeamListResponse doInBackground(Void... params) {
				return getTeams();
			}
			
			@Override
			protected void onPostExecute(TeamListResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	public CreateTeamResponse createTeam(Team team) {
		UriBuilder uri = createBuilder().addPath("teams");
		return new CreateTeamResponse(post(uri, team.toJSON()), team);
	}
	
	public void createTeam(final Team team, final CreateTeamResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateTeamResponse>() {

			@Override
			protected CreateTeamResponse doInBackground(Void... params) {
				return createTeam(team);
			}
			
			@Override
			protected void onPostExecute(CreateTeamResponse response) {
				handler.finish(response);
			}
		}).execute();
	}
	
	
	public GetTeamResponse getTeam(String teamId) {
		UriBuilder uri = createBuilder().addPath("team").addPath(teamId);
		return new GetTeamResponse(get(uri));
	}
	
	public void getTeam(final String teamId, final GetTeamResponseHandler handler) {
		(new AsyncTask<Void, Void, GetTeamResponse>() {
			@Override
			protected GetTeamResponse doInBackground(Void... params) {
				return getTeam(teamId);
			}
			
			@Override
			protected void onPostExecute(GetTeamResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}

}
