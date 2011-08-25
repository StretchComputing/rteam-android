package com.rteam.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.RTeamLog;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Game;
import com.rteam.api.common.UriBuilder;

public class GamesResource extends ResourceBase {

	/////////////////////////////////////////////////////////////////////////////////
	///// .ctor

	public GamesResource() {
		super(AndroidTokenStorage.get());
	}	
	
	/////////////////////////////////////////////////////////////////////////////////
	///// Response Classes
	
	public class CreateGameResponse extends ResourceResponse {

		private String _gameId;
		public String gameId() { return _gameId; }
		
		public CreateGameResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_gameId = json().optString("gameId");
			}
		}
	}
	
	public interface CreateGameResponseHandler {
		public void finish(CreateGameResponse response);
	}
	
	public class CreateGamesResponse extends ResourceResponse {
		
		private ArrayList<String> _gameIds;
		public ArrayList<String> gameIds() { return _gameIds; }
		
		public CreateGamesResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_gameIds = new ArrayList<String>();
				
				JSONArray gameIds = json().optJSONArray("gameIds");
				int count = gameIds != null ? gameIds.length() : 0;
				
				for(int i=0; i<count; i++) {
					_gameIds.add(gameIds.optString(i));
				}
			}
		}
	}
	
	public interface CreateGamesResponseHandler {
		public void finish(CreateGamesResponse response);
	}
	
	public class GetGameResponse extends ResourceResponse {
		private Game _game;
		public Game game() { return _game; }
		
		public GetGameResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_game = new Game(json());
			}
		}
	}
	
	public interface GetGameResponseHandler {
		public void finish(GetGameResponse response);
	}
	
	public class GetGamesResponse extends ResourceResponse {
		private Map<String, EventBase> _events;
		public Map<String, EventBase> eventsMap() { return _events; }
		public ArrayList<EventBase> events() { return new ArrayList<EventBase>(_events.values()); }
		private ArrayList<Game> _games;
		public ArrayList<Game> games() { return _games; }
		
		private String _defaultTeamId;
		
		public GetGamesResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		public GetGamesResponse(APIResponse response, String defaultTeamId) {
			super(response);
			_defaultTeamId = defaultTeamId;
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_games = new ArrayList<Game>();
				_events = new HashMap<String, EventBase>();
				
				JSONArray games = json().optJSONArray("games");
				RTeamLog.i("Response responded with : %d games.", games != null ? games.length() : -1);
				int count = games != null ? games.length() : 0;
				for(int i=0; i<count; i++) {
					Game g = new Game(games.optJSONObject(i), _defaultTeamId);
					_games.add(g);
					if (!_events.containsKey(g.eventId())) _events.put(g.eventId(), g);
				}
			}
		}
	}
	
	public interface GetGamesResponseHandler {
		public void finish(GetGamesResponse response);
	}
	
	public class UpdateGameResponse extends ResourceResponse {
		public UpdateGameResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}

	public interface UpdateGameResponseHandler {
		public void finish(UpdateGameResponse response);
	}
	
	public class DeleteGameResponse extends ResourceResponse {
		public DeleteGameResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface DeleteGameResponseHandler {
		public void finish(DeleteGameResponse response);
	}
	
	public class CastVoteResponse extends ResourceResponse {
		public CastVoteResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface CastVoteResponseHandler {
		public void finish(CastVoteResponse response);
	}
	
	public class GetVotesResponse extends ResourceResponse {
		private String _vote;
		public String vote() { return _vote; }
		
		private ArrayList<Game.Vote> _voteCounts;
		public ArrayList<Game.Vote> voteCounts() { return _voteCounts; }
		
		public GetVotesResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_vote = json().optString("vote");
				_voteCounts = new ArrayList<Game.Vote>();
				
				JSONArray votes = json().optJSONArray("members");
				int count = votes != null ? votes.length() : 0;
				
				for(int i=0; i<count; i++) {
					_voteCounts.add(new Game.Vote(votes.optJSONObject(i)));
				}
			}
		}
	}
	
	public interface GetVotesResponseHandler {
		public void finish(GetVotesResponse response);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///// Exposed Methods

	public CreateGameResponse create(Game.Create game) {
		return new CreateGameResponse(post(createBuilder().addPath("team").addPath(game.teamId()).addPath("games"), game.toJSON()));
	}
	
	public void create(final Game.Create game, final CreateGameResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateGameResponse>(){
			
			@Override
			protected CreateGameResponse doInBackground(Void... params) {
				return create(game);
			}
			
			@Override
			protected void onPostExecute(CreateGameResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	
	
	public CreateGamesResponse create(Game.CreateMultiple games) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(games.teamId())
							.addPath("games").addPath("recurring").addPath("multiple");
		return new CreateGamesResponse(post(uri, games.toJSON()));
	}
	
	public void create(final Game.CreateMultiple games, final CreateGamesResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateGamesResponse>(){
			
			@Override
			protected CreateGamesResponse doInBackground(Void... params) {
				return create(games);
			}
			
			@Override
			protected void onPostExecute(CreateGamesResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}


	public GetGameResponse get(EventBase.GetEventBase info) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(info.teamId())
							.addPath("game").addPath(info.id())
							.addPath(info.timeZone());
		return new GetGameResponse(get(uri));
	}
	
	public void get(final EventBase.GetEventBase info, final GetGameResponseHandler handler) {
		(new AsyncTask<Void, Void, GetGameResponse>() {
			
			@Override
			protected GetGameResponse doInBackground(Void... params) {
				return GamesResource.this.get(info);
			}
			
			@Override
			protected void onPostExecute(GetGameResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	
	public GetGamesResponse getForTeam(EventBase.GetAllForTeamEventBase info) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(info.teamId())
							.addPath("games")
							.addPath(info.timeZone());
		return new GetGamesResponse(get(uri), info.teamId());
	}
	
	public void getForTeam(final EventBase.GetAllForTeamEventBase info, final GetGamesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetGamesResponse>() {
			
			@Override
			protected GetGamesResponse doInBackground(Void... params) {
				return getForTeam(info);
			}
			
			@Override
			protected void onPostExecute(GetGamesResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}

	
	public GetGamesResponse getAll(EventBase.GetAllEventBase info) {
		return new GetGamesResponse(get(createBuilder().addPath("games").addPath(info.timeZone())));
	}
	
	public void getAll(final EventBase.GetAllEventBase info, final GetGamesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetGamesResponse>() {
			
			@Override
			protected GetGamesResponse doInBackground(Void... params) {
				return getAll(info);
			}
			
			@Override
			protected void onPostExecute(GetGamesResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}

	
	public UpdateGameResponse update(Game.Update update) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(update.game().teamId())
							.addPath("game").addPath(update.game().gameId());
		return new UpdateGameResponse(put(uri, update.toJSON()));
	}
	
	public void update(final Game.Update update, final UpdateGameResponseHandler handler) {
		(new AsyncTask<Void, Void, UpdateGameResponse>() {
			
			@Override
			protected UpdateGameResponse doInBackground(Void... params) {
				return update(update);
			}
			
			@Override
			protected void onPostExecute(UpdateGameResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}

	
	public DeleteGameResponse delete(Game game) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(game.teamId())
							.addPath("game").addPath(game.gameId());
		return new DeleteGameResponse(delete(uri));
	}
	
	public void delete(final Game game, final DeleteGameResponseHandler handler) {
		(new AsyncTask<Void, Void, DeleteGameResponse>() {
			
			@Override
			protected DeleteGameResponse doInBackground(Void... params) {
				return delete(game);
			}
			
			@Override
			protected void onPostExecute(DeleteGameResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}

	
	public CastVoteResponse castVote(Game.Vote vote) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(vote.game().teamId())
							.addPath("game").addPath(vote.game().gameId())
							.addPath("vote").addPath(vote.voteType().toString());
		return new CastVoteResponse(put(uri, vote.toJSON()));
	}
	
	public void castVote(final Game.Vote vote, final CastVoteResponseHandler handler) {
		(new AsyncTask<Void, Void, CastVoteResponse>() {
			
			@Override
			protected CastVoteResponse doInBackground(Void... params) {
				return castVote(vote);
			}
			
			@Override
			protected void onPostExecute(CastVoteResponse response) {
				if (handler != null) handler.finish(response);
			}
			
		}).execute();
	}

	
	public GetVotesResponse getVotes(Game game, Game.Vote.VoteType voteType) {
		UriBuilder uri = UriBuilder.create()
							.addPath("team").addPath(game.teamId())
							.addPath("game").addPath(game.gameId())
							.addPath("vote").addPath(voteType.toString())
							.addPath("tallies");
		return new GetVotesResponse(get(uri));
	}
	
	public void getVotes(final Game game, final Game.Vote.VoteType voteType, final GetVotesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetVotesResponse>() {
			
			@Override
			protected GetVotesResponse doInBackground(Void... params) {
				return getVotes(game, voteType);
			}
			
			@Override
			protected void onPostExecute(GetVotesResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
}