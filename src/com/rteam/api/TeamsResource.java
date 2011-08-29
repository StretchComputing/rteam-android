package com.rteam.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.Team;
import com.rteam.api.common.UriBuilder;

public class TeamsResource extends ResourceBase {
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// .ctor
	
	public TeamsResource() {
		super(AndroidTokenStorage.get());
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
		
		private String _teamId;
		public String getTeamId() { return _teamId; }
		
		private String _teamPageUrl; 
		public String getTeamPageUrl() { return _teamPageUrl; }
		
		private String _twitterAuthorizationUrl;
		public String getTwitterAuthorizationUrl() { return _twitterAuthorizationUrl; }

		protected CreateTeamResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_teamId = json().optString("teamId");
				_teamPageUrl = json().optString("teamPageUrl");
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
		return new CreateTeamResponse(post(createBuilder().addPath("teams"), team.toJSON()));
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
