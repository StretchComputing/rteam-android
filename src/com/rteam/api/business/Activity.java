package com.rteam.api.business;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.rteam.api.common.BitmapUtils;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;
import com.rteam.api.common.TimeZoneUtils;

public class Activity {

	///////////////////////////////////////////////////////////////////////////////
	//// Helper Classes
	
	public static abstract class ActivityFilter {
	
		//////////////////////////////////////////////////////////////////////////
		///// Members
		
		private String _timeZone;
		public String timeZone() { return _timeZone; }
		public void timeZone(String value) { _timeZone = value; }
		
		private String _teamId;
		public String teamId() { return _teamId; }
		public void teamId(String value) { _teamId = value; }
		public boolean hasTeamId() { return !StringUtils.isNullOrEmpty(teamId()); }
		
		private Integer _maxCount;
		public Integer maxCount() { return _maxCount; }
		public void maxCount(Integer value) { _maxCount = value; }
		
		private Boolean _refreshFirst;
		public Boolean refreshFirst() { return _refreshFirst; }
		public void refreshFirst(Boolean value) { _refreshFirst = value; }
				
		
		///////////////////////////////////////////////////////////////////////////
		///// .ctor
		
		protected ActivityFilter(String timeZone, String teamId, Integer maxCount, Boolean refreshFirst) {
			timeZone(timeZone);
			teamId(teamId);
			maxCount(maxCount);
			refreshFirst(refreshFirst);
		}
		
		///////////////////////////////////////////////////////////////////////////
		///// Exposed Methods
		
		public abstract ArrayList<BasicNameValuePair> toParams();
		
		protected void addParams(ArrayList<BasicNameValuePair> params) {
			if (!StringUtils.isNullOrEmpty(teamId())) 	params.add(new BasicNameValuePair("teamId", teamId()));
			if (maxCount() != null)  					params.add(new BasicNameValuePair("maxCount", Integer.toString(maxCount())));
			if (refreshFirst() != null)  				params.add(new BasicNameValuePair("refreshFirst", Boolean.toString(refreshFirst())));
		}
	}
	
	public static class NewActivityFilter extends ActivityFilter {

		/////////////////////////////////////////////////////////////////
		//// Members
		
		private boolean _newOnly;
		public boolean newOnly() { return _newOnly; }
		public void newOnly(boolean value) { _newOnly = value; }

		/////////////////////////////////////////////////////////////////
		//// .ctor

		public NewActivityFilter(boolean newOnly) {
			super(TimeZoneUtils.getTimeZone(), null, null, null);
			newOnly(newOnly);
		}
		public NewActivityFilter(String teamId, boolean newOnly) {
			super(TimeZoneUtils.getTimeZone(), teamId, null, null);
			newOnly(newOnly);
		}
		
		/////////////////////////////////////////////////////////////////
		//// Exposed
		
		@Override
		public ArrayList<BasicNameValuePair> toParams() {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			
			params.add(new BasicNameValuePair("newOnly", Boolean.toString(newOnly())));
			addParams(params);
			
			return params;
		}
	}
	
	public static class RangedActivityFilter extends ActivityFilter {
		
		/////////////////////////////////////////////////////////////////
		//// Members
		
		private Date _mostCurrentDate;
		public Date mostCurrentDate() { return _mostCurrentDate; }
		public void mostCurrentDate(Date value) { _mostCurrentDate = value; }
		
		private int _totalNumberOfDays;
		public int totalNumberOfDays() { return _totalNumberOfDays; }
		public void totalNumberOfDays(int value) { _totalNumberOfDays = value; }
		
		/////////////////////////////////////////////////////////////////
		//// .ctor
		
		public RangedActivityFilter(Date mostCurrentDate, int totalNumberOfDays) {
			super(TimeZoneUtils.getTimeZone(), null, null, null);
			mostCurrentDate(mostCurrentDate);
			totalNumberOfDays(totalNumberOfDays);
		}
		public RangedActivityFilter(String teamId, Date mostCurrentDate, int totalNumberOfDays) {
			super(TimeZoneUtils.getTimeZone(), teamId, null, null);
			mostCurrentDate(mostCurrentDate);
			totalNumberOfDays(totalNumberOfDays);
		}

		/////////////////////////////////////////////////////////////////
		//// Exposed Methods

		@Override
		public ArrayList<BasicNameValuePair> toParams() {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			
			params.add(new BasicNameValuePair("mostCurrentDate", DateUtils.toDateParameterString(mostCurrentDate())));
			params.add(new BasicNameValuePair("totalNumberOfDays", Integer.toString(totalNumberOfDays())));
			addParams(params);
			
			return params;
		}
	}
	
	

	///////////////////////////////////////////////////////////////////////////////
	//// Members
	
	private String _activityId;
	public String activityId() { return _activityId; }
	public void activityId(String value) { _activityId = value; }
	
	private String _text;
	public String text() { return _text; }
	public void text(String value) { _text = value; }
	
	private Date _createdDate;
	public Date createdDate() { return _createdDate; }
	public void createdDate(Date value) { _createdDate = value; }
	
	private String _teamName;
	public String teamName() { return _teamName; }
	public void teamName(String value) { _teamName = value; }
	
	private String _teamId;
	public String teamId() { return _teamId; } 
	public void teamId(String value) { _teamId = value; }
	
	private String _cacheId;
	public String cacheId() { return _cacheId; }
	public void cacheId(String value) { _cacheId = value; }
	
	private int _numberOfLikeVotes;
	public int numberOfLikeVotes() { return _numberOfLikeVotes; }
	public void numberOfLikeVotes(int value) { _numberOfLikeVotes = value; }
	
	private int _numberOfDislikeVotes;
	public int numberOfDislikeVotes() { return _numberOfDislikeVotes; }
	public void numberOfDislikeVotes(int value) { _numberOfDislikeVotes = value; }
	
	
	private Bitmap _thumbNail;
	public Bitmap thumbNail() { return _thumbNail; }
	public void thumbNail(Bitmap value) { _thumbNail = value; }
	
	private Bitmap _photo;
	public Bitmap photo() { return _photo; }
	public void photo(Bitmap value) { _photo = value; }
			
	private boolean _isVideo;
	public boolean isVideo() { return _isVideo; }
	public void isVideo(boolean value) { _isVideo = value; }
	
	private String _rawVideo;
	public String rawVideo() { return _rawVideo; }
	public void rawVideo(String value) { _rawVideo = value; }
	
	private String _videoPath;
	public String videoPath() { return _videoPath; }
	public void videoPath(String value) { _videoPath = value; }
	
	
	public void bindTeam(Team team) {
		if (team == null) return;
		_teamId = team.teamId();
		_teamName = team.teamName();
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//// .ctor
	
	public Activity(JSONObject json) {
		activityId(json.optString("activityId"));
		text(json.optString("text"));
		cacheId(json.optString("cacheId"));
		createdDate(DateUtils.parse(json.optString("createdDate")));
		teamName(json.optString("teamName"));
		teamId(json.optString("teamId"));
		numberOfLikeVotes(json.optInt("numberOfLikeVotes"));
		numberOfDislikeVotes(json.optInt("numberOfDislikeVotes"));
		thumbNail(BitmapUtils.getBitmapFrom(json.optString("thumbNail")));
		isVideo(json.optBoolean("isVideo"));
	}
	
	public Activity(Team team, String text) {
		teamId(team.teamId());
		teamName(team.teamName());
		text(text);
		createdDate(new Date());
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//// Exposed Methods
	
	public JSONObject toJSONCreate() {
		JSONObject json = new JSONObject();
		
		try {
			if (!StringUtils.isNullOrEmpty(text())) {
				json.put("statusUpdate", text());
			}
			
			if(photo() != null) {
				json.put("photo", BitmapUtils.getEncodedStringFrom(photo()));
				json.put("isPortrait", photo().getWidth() > photo().getHeight());
			}
			if (rawVideo() != null) {
				json.put("video", rawVideo());
			}
		} catch (JSONException e) {}
		
		return json;
	}
}
