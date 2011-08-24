package com.rteam.api.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.api.common.DateUtils;
import com.rteam.api.common.EnumUtils;
import com.rteam.api.common.TimeZoneUtils;

public abstract class EventBase implements Serializable, Comparable<EventBase> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract class CreateEventBase {
		//////////////////////////////////////////////////////////////////////
		/// Members
		
		private Message.NotificationType _notificationType;
		public Message.NotificationType notificationType() { return _notificationType; }
		public void notificationType(Message.NotificationType value) { _notificationType = value; }	
		
		private String _timeZone;
		public String timeZone() { return _timeZone; }
		public void timeZone(String value ) { _timeZone = value; }
		
				
		protected abstract EventBase event();		
		
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			
			try {
				json.put("timeZone", timeZone());
				json.put("notificationType", notificationType().toString());
				addGame(json);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return json;
		}
				
		protected void addGame(JSONObject json) throws JSONException {
			event().appendToCreateJSON(json);
		}
		
		public String teamId() { return event().teamId(); }
	}
	
	public abstract class CreateMultipleEventBase extends CreateEventBase {
		
		//////////////////////////////////////////////////////////////////////
		/// Members
				
		protected abstract ArrayList<EventBase> events();
		protected abstract String eventsKey();
			
		@Override
		protected void addGame(JSONObject json) throws JSONException {
			JSONArray eventArray = new JSONArray();
			
			for(EventBase event : events()) {
				JSONObject eventJSON = new JSONObject();
				event.appendToCreateJSON(eventJSON);
				eventArray.put(eventJSON);
			}
			
			json.put(eventsKey(), eventArray);
		}
		
		@Override
		public String teamId() { return events().get(0).teamId(); }
	}
	
	public static abstract class UpdateEventBase {
		private Message.NotificationType _notificationType = Message.NotificationType.None;
		public Message.NotificationType notificationType() { return _notificationType; }
		public void notificationType(Message.NotificationType value) { _notificationType = value; }	
		
		protected abstract EventBase event();
		
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			
			try {
				event().appendToUpdateJSON(json);
				json.put("notificationType", notificationType().toString());
			} catch (JSONException e) { e.printStackTrace(); }
			
			return json;
		}
	}	
	
	public static class GetEventBase extends GetAllForTeamEventBase {
		private String _id;
		public String id() { return _id; }
		public void id(String value) { _id = value; }
		
		////////////////////////////////////////////////////////////////////////////////////
		//// .ctor
		
		public GetEventBase(String id, String teamId, Event.Type eventType) {
			super(teamId, eventType);
			_id = id;
		}
		
		public GetEventBase(String id, String teamId, String timeZone, Event.Type eventType) {
			super(teamId, timeZone, eventType);
			_id = id;
		}
	}
	
	public static class GetAllForTeamEventBase extends GetAllEventBase {
		private String _teamId;
		public String teamId() { return _teamId; }
		public void teamId(String value) { _teamId = value; }
		
		////////////////////////////////////////////////////////////////////////////////////
		//// .ctor
		
		public GetAllForTeamEventBase(String teamId, Event.Type eventType) {
			super(eventType);
			_teamId = teamId;
		}
		
		public GetAllForTeamEventBase(String teamId, String timeZone, Event.Type eventType) {
			super(timeZone, eventType);
			_teamId = teamId;
		}
	}
	
	public static class GetAllEventBase {		
		private String _timeZone;
		public String timeZone() { return _timeZone; }
		public void timeZone(String value) { _timeZone = value; }
		
		private Event.Type _eventType;
		public Event.Type eventType() { return _eventType; }
		public void eventType(Event.Type value) { _eventType = value; }
		
		
		////////////////////////////////////////////////////////////////////////////////////
		//// .ctor
		
		public GetAllEventBase(Event.Type eventType) {
			this(TimeZoneUtils.getTimeZone(), eventType);
		}
		
		public GetAllEventBase(String timeZone, Event.Type eventType) {
			_timeZone = timeZone;
			_eventType = eventType;
		}
	}

	private Date _startDate;
	public Date startDate() { return _startDate; }
	public void startDate(Date value) { _startDate = value; }
	
	private Date _endDate;
	public Date endDate() { return _endDate; }
	public void endDate(Date value) { _endDate = value; }
	

	private String _description;
	public String description() { return _description; }
	public void description(String value) { _description = value; }

	private String _latitude;
	public String latitude() { return _latitude; }
	public void latitude(String value) { _latitude = value; }
	
	private String _longitude;
	public String longitude() { return _longitude; }
	public void longitude(String value) { _longitude = value; }
	
	private String _location;
	public String location() { return _location; }
	public void location(String value) { _location = value; }
	
	private String _opponent;
	public String opponent() { return _opponent; }
	public void opponent(String value) { _opponent = value; }
	
	private Event.Type _eventType;
	public Event.Type eventType() { return _eventType; }
	public void eventType(Event.Type value) { _eventType = value; }
	
	private String _eventName;
	public String eventName() { return _eventName; }
	public void eventName(String value) { _eventName = value; }
	
	private String _teamId;
	public String teamId() { return _teamId; }
	public void teamId(String value) { _teamId = value; }
	
	private String _teamName;
	public String teamName() { return _teamName; }
	public void teamName(String value) { _teamName = value; }
	
	private Member.Role _participantRole;
	public Member.Role participantRole() { return _participantRole; }
	public void participantRole(Member.Role value) { _participantRole = value; }		
	
	public void bindTeam(Team team) {
		_teamId = team.teamId();
		_teamName = team.teamName();
	}

	protected EventBase() {}
	protected EventBase(JSONObject json) {
		_startDate = DateUtils.parse(json.optString("startDate"));
		_endDate = DateUtils.parse(json.optString("endDate"));
		_description = json.optString("description");
		_latitude = json.optString("latitude");
		_longitude = json.optString("longitude");
		_opponent = json.optString("opponent");
		_eventType = EnumUtils.fromString(Event.Type.class, json.optString("eventType"), Event.Type.Game);	// Defaults to game if it's missing
		_eventName = json.optString("eventName");
		
		_teamId = json.optString("teamId");
		_teamName = json.optString("teamName");
		_participantRole = EnumUtils.fromString(Member.Role.class, json.optString("participantRole"));

	}
	
	public boolean isGame() { return eventType() == null || eventType() == Event.Type.Game; }
	
	
	protected void appendToJSON(JSONObject json) throws JSONException {
		if (!isGame()) {
			json.put("eventType", eventType().toString());
			json.put("eventName", eventName());
		}
		
		json.put("startDate", DateUtils.toFullString(startDate()));
		json.put("endDate", DateUtils.toFullString(endDate()));
		json.put("description", description());
		json.put("latitude", latitude());
		json.put("longitude", longitude());
		json.put("location", location());
		json.put("opponent", opponent());		
	}
	
	public abstract String eventId();
	
	public abstract void appendToCreateJSON(JSONObject json) throws JSONException;
	public abstract void appendToUpdateJSON(JSONObject json) throws JSONException;
	
	public String toPrettyString(){
		return eventType().toString() + " vs. " + opponent();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EventBase)) return false;
		EventBase other = (EventBase) o;
		
		if (eventId() == null || other.eventId() == null) return false;
		return eventId() == other.eventId();
	}
	
	@Override
	public int hashCode() {
		if (eventId() == null) return super.hashCode();
		return eventId().hashCode();
	}
	
	@Override
	public int compareTo(EventBase other) {
		return startDate().compareTo(other.startDate());
	}
	
	public boolean isInProgress() { 
		Date today = new Date();
		return today.after(startDate()) && today.before(endDate());
	}
	
	public boolean isUpcomingToday() {
		return new Date().after(startDate()) && DateUtils.isToday(startDate());
	}
	
	public boolean isTomorrow() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		return DateUtils.isSameDay(startDate(), c.getTime());
	}
}
