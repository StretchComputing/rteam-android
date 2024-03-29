package com.rteam.api.business;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.business.Member.Role;
import com.rteam.api.common.EnumUtils;

public class Game extends EventBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class GameInterval {
		private static final int CancelledInterval = -4; 
		private static final int NAInterval = -3;
		private static final int OvertimeInterval = -2;
		private static final int GameOverInterval = -1;
		private static final int GameNotStartedInterval = 0;
		
		public static final GameInterval GameCancelled = new GameInterval(CancelledInterval);
		public static final GameInterval GameInProgressNoTimeInterval = new GameInterval(NAInterval);
		public static final GameInterval GameCurrentlyOvertime = new GameInterval(OvertimeInterval);
		public static final GameInterval GameOver = new GameInterval(GameOverInterval);
		public static final GameInterval GameNotStarted = new GameInterval(GameNotStartedInterval);
		
		
		public boolean isCancelled()  { return _interval == GameCancelled.getInterval(); }
		public boolean isInProgress() { return _interval == GameInProgressNoTimeInterval.getInterval() || _interval > 0; }
		public boolean isGameOver()   { return _interval == GameOver.getInterval(); }
		public boolean isInOvertime() { return _interval == GameCurrentlyOvertime.getInterval(); }
		public boolean isNotStarted() { return _interval == GameNotStarted.getInterval(); }
				
		private int _interval;
		public int getInterval() { return _interval; }
		
		public void increase() { if (_interval >= 0) _interval++; }
		public void decrease() { if (_interval > 0) _interval--; }
		
		private GameInterval(int interval) { _interval = interval; }
		
		@Override
		public String toString() {
			if (_interval > 0) return Integer.toString(_interval);
			switch (_interval) {
			case CancelledInterval: return "Cancelled";
			case NAInterval: return "N/A";
			case OvertimeInterval: return "Overtime";
			case GameOverInterval: return "Game Over";
			case GameNotStartedInterval: return "Not Started";
			}
			return "";
		}
	}
	
	public class Create extends CreateEventBase {
		private Game _game;
		public Game game() { return _game; }
		public void Game(Game value) { _game = value; }
		
		@Override
		protected EventBase event() {
			return _game;
		}		
	}
	
	public class CreateMultiple extends CreateMultipleEventBase {
		
		private ArrayList<Game> _games;
		public ArrayList<Game> games() { return _games; }
		public void games(ArrayList<Game> value) { _games = value; }

		@Override
		protected ArrayList<EventBase> events() {
			ArrayList<EventBase> asEvents = new ArrayList<EventBase>();
			for(Game g : _games) asEvents.add(g);
			return asEvents;
		}

		@Override
		protected String eventsKey() { return "games"; }

		@Override
		protected EventBase event() { return null; }
	}
	
	public static class Update extends UpdateEventBase {
		private Game _game;
		public Game game() { return _game; }
		public void game(Game value) { _game = value; }
		
		public Update(Game game) {
			super();
			_game = game; 
		}
		
		@Override
		protected EventBase event() { return _game; }
	}
	
	
	public static class Vote {
		public enum VoteType { 
			MVP;
			
			@Override
			public String toString() { return super.toString().toLowerCase(); }
		}
		
		private String _memberId;
		public String memberId() { return _memberId; }
		public void memberId(String value) { _memberId = value; }
		
		private String _memberName;
		public String memberName() { return _memberName; }
		public void memberName(String value) { _memberName = value; }
		
		private int _voteCount;
		public int voteCount() { return _voteCount; }
		public void voteCount(int value) { _voteCount = value; }
		
		private VoteType _voteType;
		public VoteType voteType() { return _voteType; }
		public void voteType(VoteType value) { _voteType = value; }
		
		public Game _game;
		public Game game() { return _game; }
		public void game(Game value) { _game = value; }
		
		
		public Vote() {}
		
		public Vote(JSONObject json) {		
			_memberId = json.optString("memberId");
			_memberName = json.optString("memberName");
			_voteCount = json.optInt("voteCount");
		}
		
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			
			try {
				json.put("memberId", _memberId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	/// Members
			
	private int _scoreUs;
	public int scoreUs() { return _scoreUs; }
	public void scoreUs(int value) { _scoreUs = value; }
	
	private int _scoreThem;
	public int scoreThem() { return _scoreThem; }
	public void scoreThem(int value) { _scoreThem = value; }
	
	private GameInterval _interval = GameInterval.GameNotStarted;
	public GameInterval interval() { return _interval; }
	public void interval(GameInterval value) { _interval = value; }
	
	private Poll.Status _pollStatus;
	public Poll.Status pollStatus() { return _pollStatus; }
	public void pollStatus(Poll.Status value) { _pollStatus = value; }
	
	private String _mvpMemberId;
	public String mvpMemberId() { return _mvpMemberId; }
	public void mvpMemberId(String value) { _mvpMemberId = value; }
	
	private String _mvpDisplayName;
	public String mvpDisplayName() { return _mvpDisplayName; }
	public void mvpDisplayName(String value) { _mvpDisplayName = value; }
	
	
	private String _gameId;
	public String gameId() { return _gameId; }
	public void gameId(String value) { _gameId = value; }
	
	public String eventId() { return _gameId; }
	
	private Team _team;
	
	public String intervalName() {
		if (_team == null && teamId() != null) {
			_team = TeamCache.get(teamId());
		}
		
		if (_team == null || _team.sport() == null) {
			return Team.Sport.DefaultIntervalName;
		}
		
		return _team.sport().intervalName();
	}
	
	public Game() {
		eventType(Event.Type.Game);
	}
	
	public Game(JSONObject json) {
		this(json, null);
	}
	public Game(JSONObject json, Team defaultTeam) {	
		super(json);

		_scoreUs = json.optInt("scoreUs", 0);
		_scoreThem = json.optInt("scoreThem", 0);
		_interval = new GameInterval(json.optInt("interval", 0));
		_pollStatus = EnumUtils.fromString(Poll.Status.class, json.optString("pollStatus"));
		_mvpMemberId = json.optString("mvpMemberId");
		_mvpDisplayName = json.optString("mvpDisplayName");
		
		_gameId = json.optString("gameId");
		
		if (defaultTeam != null) {
			_team = defaultTeam;
			teamId(defaultTeam.teamId());
			if (participantRole() == null || participantRole() == Role.Unknown) {
				participantRole(defaultTeam.participantRole());
			}
		}
	}
	
	

	public void appendToCreateJSON(JSONObject json) throws JSONException {
		appendToJSON(json);
	}
	
	public void appendToUpdateJSON(JSONObject json) throws JSONException {
		appendToJSON(json);
		json.put("scoreUs", scoreUs());
		json.put("scoreThem", scoreThem());
		if (interval() != null) json.put("interval", interval().getInterval());
		if (pollStatus() != null) json.put("pollStatus", pollStatus().toString());
	}	
}
