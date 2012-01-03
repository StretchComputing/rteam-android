package com.rteam.api.business;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.rteam.api.common.BitmapUtils;
import com.rteam.api.common.EnumUtils;

public class Team {

	public static class Sport {
		public static final String DefaultIntervalName = "Interval";
		
		public static final Sport Football = new Sport("Football", false, "Quarter");
		public static final Sport Basketball = new Sport("Basketball", false, "Quarter");
		public static final Sport Soccer = new Sport("Soccer", false, "Period");
		public static final Sport Baseball = new Sport("Baseball", false, "Inning");
		public static final Sport Hockey = new Sport("Hockey", false, "Period");
		public static final Sport Lacrosse = new Sport("Lacrosse", false, "Period");
		public static final Sport Tennis = new Sport("Tennis", false, DefaultIntervalName);
		public static final Sport Volleyball = new Sport("Volleyball", false, DefaultIntervalName);
		
		private String _text;
		private boolean _isOther;
		public boolean isOther() { return _isOther; }
		
		private String _intervalName;
		public String intervalName() { return _intervalName; }
		
		public Sport(String text) {
			this(text, true, DefaultIntervalName);
		}
		private Sport(String text, boolean isOther, String intervalName) {
			_text = text;
			_isOther = isOther;
			_intervalName = intervalName;
		}
					
		@Override
		public String toString() {
			return _text;
		}
		
		public boolean equals(String value) {
			return toString().equalsIgnoreCase(value);
		}
		
		public static Sport fromString(String value) {
			if (Football.equals(value)) return Football;
			if (Basketball.equals(value)) return Basketball;
			if (Soccer.equals(value)) return Soccer;
			if (Baseball.equals(value)) return Baseball;
			if (Hockey.equals(value)) return Hockey;
			if (Lacrosse.equals(value)) return Lacrosse;
			if (Tennis.equals(value)) return Tennis;
			if (Volleyball.equals(value)) return Volleyball;
			
			return new Sport(value);
		}

	}
		
	/////////////////////////////////////////////////////////////////////////////////
	/// Members

	private String _teamId;
	public String teamId() { return _teamId; }
	public void teamId(String value) { _teamId = value; }
	
	private String _teamName;
	public String teamName() { return _teamName; }
	public void teamName(String value) { _teamName = value; }
	
	private String _teamSiteUrl;
	public String teamSiteUrl() { return _teamSiteUrl; }
	public void teamSiteUrl(String value) { _teamSiteUrl = value; }
	
	private Member.Role _participantRole;
	public Member.Role participantRole() { return _participantRole; }
	public void participantRole(Member.Role value) { _participantRole = value; }
	
	private Team.Sport _sport;
	public Team.Sport sport() { return _sport; }
	public void sport(Team.Sport value) { _sport = value; }
	
	
	/////////////////////////////////////////////////////////////////////////////////
	//// Not always required
	
	private String _leagueName;
	public String leagueName() { return _leagueName; }
	public void leagueName(String value) { _leagueName = value; }
	
	private String _description;
	public String description() { return _description; }
	public void description(String value) { _description = value; }

	private boolean _usesTwitter;
	public boolean usesTwitter() { return _usesTwitter; }
	public void usesTwitter(boolean value) { _usesTwitter = value; }

	private String _teamPageUrl;
	public String teamPageUrl() { return _teamPageUrl; }
	public void teamPageUrl(String value) { _teamPageUrl = value; }
	
	private String _gender;
	public String gender() { return _gender; }
	public void gender(String value) { _gender = value; }
	
	private String _city;
	public String city() { return _city; }
	public void city(String value) { _city = value; }
	
	private String _state;
	public String state() { return _state; }
	public void state(String value) { _state = value; }
	
	private String _latitude;
	public String latitude() { return _latitude; }
	public void latitude(String value) { _latitude = value; }
	
	private String _longitude;
	public String longitude() { return _longitude; }
	public void longitude(String value) { _longitude = value; }
	
	private Bitmap _thumbNail;
	public Bitmap thumbNail() { return _thumbNail; }
	public void thumbNail(Bitmap value) { _thumbNail = value; }
	
	private Bitmap _photo;
	public Bitmap photo() { return _photo; }
	public void photo(Bitmap value) { _photo = value; }

		
	/////////////////////////////////////////////////////////////////////////////////
	/// .ctor
	
	public Team() {
		
	}
	
	public Team(JSONObject json) {
		teamId(json.optString("teamId"));
		teamName(json.optString("teamName"));
		teamSiteUrl(json.optString("teamSiteUrl"));
		participantRole(EnumUtils.fromString(Member.Role.class, json.optString("participantRole"), Member.Role.Unknown));
		sport(Sport.fromString(json.optString("sport")));
		
		leagueName(json.optString("leagueName"));
		description(json.optString("description"));
		usesTwitter(json.optBoolean("useTwitter"));
		teamPageUrl(json.optString("teamPageUrl"));
		gender(json.optString("gender"));
		city(json.optString("city"));
		state(json.optString("state"));
		latitude(json.optString("latitude"));
		longitude(json.optString("longitude"));
		
		thumbNail(BitmapUtils.getBitmapFrom(json.optString("thumbNail")));
		photo(BitmapUtils.getBitmapFrom(json.optString("photo")));
	}
	
	@Override
	public String toString() { return teamName(); }
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.putOpt("teamName", teamName());
			json.putOpt("description", description());
			json.putOpt("leagueName", leagueName());
			json.putOpt("sport", sport());
			json.putOpt("useTwitter", usesTwitter());
			json.putOpt("gender", gender());
			json.putOpt("city", city());
			json.putOpt("state", state());
			json.putOpt("latitude", latitude());
			json.putOpt("longitude", longitude());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
}
