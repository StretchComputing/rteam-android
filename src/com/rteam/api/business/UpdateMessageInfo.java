package com.rteam.api.business;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class UpdateMessageInfo {
	
	private String _teamId;
	public String teamId() { return _teamId; }
	private String _messageId;
	public String messageId() { return _messageId; }

	//////////////////////////////////////
	//// .ctors
	protected UpdateMessageInfo(MessageInfo message) {
		_teamId = message.teamId();
		_messageId = message.messageThreadId();
	}
	
	public abstract JSONObject toJSON();
	

	public static class Viewed extends UpdateMessageInfo {
		private boolean _wasViewed;
		
		public Viewed(MessageInfo message) {
			this(message, message.wasViewed());
		}
		public Viewed(MessageInfo message, boolean wasViewed) {
			super(message);
			_wasViewed = wasViewed;
		}
		
		@Override
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			
			try {
				json.put("wasViewed", _wasViewed);
			} catch (JSONException e) {}
			
			return json;
		}
	}
	
	public static class Reply extends UpdateMessageInfo {
		private String _reply;
		
		public Reply(MessageInfo message, String reply) {
			super(message);
			_reply = reply;
		}
		
		@Override
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			
			try {
				json.put("reply", _reply);
			} catch (JSONException e) {}
			
			return json;
		}
	}
	
	public static class Delete extends UpdateMessageInfo {
		private Message.Status _status;
		
		public Delete(MessageInfo message) {
			super(message);
			_status = message.status();
		}
		
		@Override
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			
			try {
				json.put("status", _status.toString());
			} catch(JSONException e) {}
			
			return json;
		}
	}
	
	// TODO : Implement the others
}


