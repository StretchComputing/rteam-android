package com.rteam.api.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.rteam.api.common.StringUtils;
import com.rteam.api.common.TimeZoneUtils;

public class MessageFilters {
	
	/////////////////////////////////////////////////////////////////
	/// Meta Data (Ignored in Map)
	private String _timeZone;
	public String timeZone() { return _timeZone; }
	public void timeZone(String value) { _timeZone = value; }
	
	private String _teamId;
	public String teamId() { return _teamId; }
	public void teamId(String value) { _teamId = value; }
	public boolean hasTeamId() { return !StringUtils.isNullOrEmpty(teamId()); }
	
	
	/////////////////////////////////////////////////////////////////
	/// Actual Filters -- Used in Map
	private Message.Group _messageGroup;
	public Message.Group messageGroup() { return _messageGroup; }
	public void messageGroup(Message.Group value) { _messageGroup = value; }
	
	private String _eventId;
	public String eventId() { return _eventId; }
	public void eventId(String value) { _eventId = value; }
	
	private Event.Type _eventType;
	public Event.Type eventType() { return _eventType; }
	public void eventType(Event.Type value) { _eventType = value; }
	
	private Message.Status _status;
	public Message.Status status() { return _status; }
	public void status(Message.Status value) { _status = value; }
	
	private Boolean _wasViewed;
	public Boolean wasViewed() { return _wasViewed; }
	public void wasViewed(Boolean value) { _wasViewed = value; }
	
	private Boolean _includeBodyAndChoices;
	public Boolean includeBodyAndChoices() { return _includeBodyAndChoices; }
	public void includeBodyAndChoices(Boolean value) { _includeBodyAndChoices = value; }
	
	public MessageFilters() {
		timeZone(TimeZoneUtils.getTimeZone());
		messageGroup(null);
		eventId(null);
		eventType(null);
		status(null);
		wasViewed(null);
		includeBodyAndChoices(null);
	}
	
	
	public Map<String, String> toMap() {
		Map<String, String> params = new HashMap<String, String>();
		
		if (messageGroup() != null) params.put("messageGroup", messageGroup().toString());
		if (eventId() != null) { 
			params.put("eventId", eventId());
			params.put("eventType", eventType().toString());
		}
		if (status() != null) params.put("status", status().toString());
		if (wasViewed() != null) params.put("wasViewed", wasViewed().toString());
		if (includeBodyAndChoices() != null) params.put("includeBodyAndChoices", includeBodyAndChoices().toString());
		
		return params;	
	}
	
	public ArrayList<BasicNameValuePair> getParams() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		Map<String, String> mapParams = toMap();
		
		for(String key : mapParams.keySet()) {
			params.add(new BasicNameValuePair(key, mapParams.get(key)));
		}
		
		return params;
	}
}
