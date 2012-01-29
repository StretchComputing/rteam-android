package com.rteam.api.business;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.api.common.EnumUtils;

public class Attendance {
	
	/////////////////////////////////////////////////////////////////////////////////
	//// Helper Classes 
	
	public static class Attendee {
		private Boolean _present;
		public Boolean present() { return _present; }
		public void present(Boolean _present) { this._present = _present; }
		
		private String _memberId;
		public String memberId() { return _memberId; }
		public void memberId(String _memberId) { this._memberId = _memberId; }
		
		private String _memberName;
		public String memberName(){ return _memberName; }
		public void memberName(String value) { _memberName = value; }
		
		private String _teamId;
		public void teamId(String _teamId) { this._teamId = _teamId; }
		public String teamId() { return _teamId; }
		
		private String _eventId;
		public void eventId(String _eventId) { this._eventId = _eventId; }
		public String eventId() { return _eventId; }
		
		private Event.Type _eventType;
		public void eventType(Event.Type _eventType) { this._eventType = _eventType; }
		public Event.Type eventType() { return _eventType; }
		
		public Attendee() {}
		public Attendee(JSONObject json) {
			memberId(json.optString("memberId"));
			if (json.has("present")) {
				present(json.optString("present").equalsIgnoreCase("yes"));
			}
			
			teamId(json.optString("teamId"));
			eventId(json.optString("eventId"));
			eventType(EnumUtils.fromString(Event.Type.class, json.optString("eventType"), Event.Type.Generic));
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			
			json.put("memberId", memberId());
			json.put("present", present() ? "yes" : "no");
			
			return json;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	private String _teamId;
	public void teamId(String _teamId) { this._teamId = _teamId; }
	public String teamId() { return _teamId; }
	
	private String _eventId;
	public void eventId(String _eventId) { this._eventId = _eventId; }
	public String eventId() { return _eventId; }
	
	private Event.Type _eventType;
	public void eventType(Event.Type _eventType) { this._eventType = _eventType; }
	public Event.Type eventType() { return _eventType; }
	
	private ArrayList<Attendee> _attendees;
	public void attendees(ArrayList<Attendee> _attendees) { this._attendees = _attendees; }
	public ArrayList<Attendee> attendees() { return _attendees; }


	/////////////////////////////////////////////////////////////////////////////////
	//// .ctor

	public Attendance(JSONObject json) {	
		teamId(json.optString("teamId"));
		eventId(json.optString("eventId"));
		eventType(EnumUtils.fromString(Event.Type.class, json.optString("eventType"), Event.Type.None));
		
		ArrayList<Attendee> aa = new ArrayList<Attendee>();
		JSONArray a = json.optJSONArray("attendees");
		int count = a != null ? a.length() : 0;
		for (int i=0; i<count; i++) {
			aa.add(new Attendee(a.optJSONObject(i)));			
		}
		attendees(aa);
	}
	
	public void addEventInfo(EventBase event) {
		eventId(event.eventId());
		eventType(event.eventType());
		teamId(event.teamId());
	}
	
	public void addMemberInfo(Member member) {
		Attendee memberAttendee = findAttendee(member.memberId());
		if (memberAttendee == null) {
			memberAttendee = new Attendee();
			_attendees.add(memberAttendee);
		}
		
		memberAttendee.memberId(member.memberId());
		memberAttendee.memberName(member.memberName());
	}
	
	private Attendee findAttendee(String memberId) {
		for(Attendee a : _attendees) {
			if (a.memberId().equalsIgnoreCase(memberId)) {
				return a;
			}
		}
		return null;
	}
	
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.putOpt("teamId", teamId());
			json.putOpt("eventId", eventId());
			json.putOpt("eventType", eventType().toString());
			json.putOpt("attendees", attendeesToJSON());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	private JSONArray attendeesToJSON() throws JSONException {
		JSONArray array = new JSONArray();
		
		for (Attendee a : attendees()) {
			if (a.present() != null) {
				array.put(a.toJSON());
			}
		}
		
		return array;
	}
}
