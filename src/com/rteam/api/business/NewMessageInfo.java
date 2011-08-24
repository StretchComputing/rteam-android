package com.rteam.api.business;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.api.common.JSONUtils;

public class NewMessageInfo {

	private String _subject;
	public String subject() { return _subject; }
	public void subject(String value) { _subject = value; }
	
	private String _body;
	public String body() { return _body; }
	public void body(String value) { _body = value; }
	
	private Message.Type _type;
	public Message.Type type() { return _type; }
	public void type(Message.Type value) { _type = value; }
	
	private String _eventId;
	public String eventId() { return _eventId; }
	public void eventId(String value) { _eventId = value; }
	
	private Event.Type _eventType;
	public Event.Type eventType() { return _eventType; }
	public void eventType(Event.Type value) { _eventType = value; }
	
	private Collection<String> _pollChoices;
	public Collection<String> pollChoices() { return _pollChoices; }
	public void pollChoices(Collection<String> value) { _pollChoices = value; }
	
	private Collection<String> _recipients;
	public Collection<String> recipients() { return _recipients; }
	public void recipients(Collection<String> value) { _recipients = value; }
	
	private Boolean _isAlert;
	public Boolean isAlert() { return _isAlert; }
	public void isAlert(Boolean value) { _isAlert = value; }
	
	private Boolean _includeFans;
	public Boolean includeFans() { return _includeFans; }
	public void includeFans(Boolean value) { _includeFans = value; }
	
	private Boolean _isPublic;
	public Boolean isPublic() { return _isPublic; }
	public void isPublic(Boolean value) { _isPublic = value; }
	
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("subject", subject());
			json.put("body", body());
			json.put("type", type().toString());
			if (eventId() != null) {
				json.put("eventId", eventId());
				json.put("eventType", eventType().toString());
			}
			JSONArray arr = JSONUtils.convertToArray(pollChoices());
			if (arr != null) json.put("pollChoices", arr);
			arr = JSONUtils.convertToArray(recipients());
			if (arr != null) json.put("recipients", arr);
			if (isAlert() != null) json.put("isAlert", isAlert());
			if (includeFans() != null) json.put("includeFans", includeFans());
			if (isPublic() != null) json.put("isPublic", isPublic());
		} catch(JSONException e) {}
		
		return json;
	}
}
