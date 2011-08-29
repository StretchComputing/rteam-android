package com.rteam.api.business;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.api.common.StringUtils;

public class Practice extends EventBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public class Create extends CreateEventBase {
		private Practice _practice;
		public Practice practice() { return _practice; }
		public void Practice(Practice value) { _practice = value; }
		
		@Override
		protected EventBase event() { return _practice; }
	}
	
	public class CreateMultiple extends CreateMultipleEventBase {
		private ArrayList<Practice> _practices;
		public ArrayList<Practice> practices() { return _practices; }
		public void practices(ArrayList<Practice> value) { _practices = value; }
		@Override
		protected ArrayList<EventBase> events() {
			ArrayList<EventBase> asEvents = new ArrayList<EventBase>();
			for(Practice p : practices()) asEvents.add(p);
			return asEvents;
		}
		@Override
		protected String eventsKey() { return "practices"; }
		@Override
		protected EventBase event() { return null; }
	}
	
	public static class Update extends UpdateEventBase {
		private Practice _practice;
		public Practice practice() { return _practice; }
		public void practice(Practice value) { _practice = value; }
		
		@Override
		protected EventBase event() { return _practice; }
		
		public Update(Practice practice) {
			super();
			_practice = practice;
		}
	}
	
	private String _practiceId;
	public String practiceId() { return _practiceId; }
	public void practiceId(String value) { _practiceId = value; }
	
	public String eventId() { return _practiceId; }
	
	public Practice() { 
		eventType(Event.Type.Practice);
	}
	
	public Practice(JSONObject json) { this(json, null); }
	public Practice(JSONObject json, String defaultTeamId) {
		super(json);

		_practiceId = json.optString("practiceId");
		
		if (!StringUtils.isNullOrEmpty(defaultTeamId)) teamId(defaultTeamId);
	}

	
	@Override
	public void appendToCreateJSON(JSONObject json) throws JSONException {
		appendToJSON(json);
	}
	@Override
	public void appendToUpdateJSON(JSONObject json) throws JSONException {
		json.put("practiceId", practiceId());
		appendToJSON(json);
	}
}
