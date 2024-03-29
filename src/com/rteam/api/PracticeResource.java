package com.rteam.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.RTeamApplicationVersion;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Practice;
import com.rteam.api.common.UriBuilder;

public class PracticeResource extends ResourceBase {

	//////////////////////////////////////////////////////////////////////////////////////
	///// .ctor
	
	public static PracticeResource instance() {
		if (_instance == null) _instance = new PracticeResource();
		return _instance;
	}
	private static PracticeResource _instance;
	
	private PracticeResource() {
		super(AndroidTokenStorage.get(), RTeamApplicationVersion.get());
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	///// Response Classes
	
	public class CreatePracticeResponse extends ResourceResponse {
		protected CreatePracticeResponse(APIResponse response, Practice practice) {
			super(response);
			initialize(practice);
		}
		
		private void initialize(Practice practice) {
			if (isResponseGood()) {
				practice.practiceId(json().optString("practiceId"));
			}
		}
	}
	
	public interface CreatePracticeResponseHandler {
		public void finish(CreatePracticeResponse response);
	}
	
	public class CreatePracticesResponse extends ResourceResponse {
		protected CreatePracticesResponse(APIResponse response, List<Practice> practices) {
			super(response);
			initialize(practices);
		}
		
		private void initialize(List<Practice> practices) {
			if (isResponseGood()) {
				JSONArray practiceIds = json().optJSONArray("practiceIds");
				int count = practiceIds != null ? practiceIds.length() : 0;
				
				for (int i = 0; i < count && i < practices.size(); i++) {
					practices.get(i).practiceId(practiceIds.optString(i));
				}
			}
		}
	}

	public interface CreatePracticesResponseHandler {
		public void finish(CreatePracticesResponse response);
	}
	
	
	public class GetPracticeResponse extends ResourceResponse {
		private Practice _practice;
		public Practice practice() { return _practice; }
		
		public GetPracticeResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_practice = new Practice(json());
			}
		}
	}

	public interface GetPracticeResponseHandler {
		public void finish(GetPracticeResponse response);
	}
	
	
	public class GetPracticesResponse extends ResourceResponse {
		private Map<String, EventBase> _events;
		public Map<String, EventBase> eventsMap() { return _events; }
		public ArrayList<EventBase> events() { return new ArrayList<EventBase>(_events.values()); }
		
		private ArrayList<Practice> _practices;
		public ArrayList<Practice> practices() { return _practices; }
		
		private ArrayList<Practice> _practicesToday;
		public ArrayList<Practice> practicesToday() { return _practicesToday; }
		
		private ArrayList<Practice> _practicesTomorrow;
		public ArrayList<Practice> practicesTomorrow() { return _practicesTomorrow; }
		
		protected GetPracticesResponse(APIResponse response) {
			this(response, null);
		}
		protected GetPracticesResponse(APIResponse response, EventBase.GetAllForTeamEventBase info) {
			super(response);
			initialize(info);
		}
		
		private void initialize(EventBase.GetAllForTeamEventBase info) {
			if (isResponseGood()) {
				_practices = new ArrayList<Practice>();
				_events = new HashMap<String, EventBase>();
				
				JSONArray practices = json().optJSONArray("practices");
				int count = practices != null ? practices.length() : 0;
				for(int i=0; i<count; i++) {
					Practice practice = new Practice(practices.optJSONObject(i), info != null ? info.team() : null);
					_practices.add(practice);
					if (!_events.containsKey(practice.eventId())) _events.put(practice.eventId(), practice);
				}
				
				_practicesToday = new ArrayList<Practice>();				
				practices = json().optJSONArray("today");
				count = practices != null ? practices.length() : 0;
				for(int i=0; i<count; i++) {
					Practice practice = new Practice(practices.optJSONObject(i), info != null ? info.team() : null);
					_practicesToday.add(practice);
					_practices.add(practice);
					if (!_events.containsKey(practice.eventId())) _events.put(practice.eventId(), practice);
				}
				
				_practicesTomorrow = new ArrayList<Practice>();
				practices = json().optJSONArray("tomorrow");
				count = practices != null ? practices.length() : 0;
				for(int i=0; i<count; i++) {
					Practice practice = new Practice(practices.optJSONObject(i), info != null ? info.team() : null);
					_practicesTomorrow.add(practice);
					_practices.add(practice);
					if (!_events.containsKey(practice.eventId())) _events.put(practice.eventId(), practice);
				}
			}
		}
	}
	
	public interface GetPracticesResponseHandler {
		public void finish(GetPracticesResponse response);
	}
	
	public class UpdatePracticeResponse extends ResourceResponse {
		public UpdatePracticeResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}

	public interface UpdatePracticeResponseHandler {
		public void finish(UpdatePracticeResponse response);
	}
	
	public class DeletePracticeResponse extends ResourceResponse {
		public DeletePracticeResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface DeletePracticeResponseHandler {
		public void finish(DeletePracticeResponse response);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	///// Exposed Methods

	
	public CreatePracticeResponse create(Practice.Create create) {
		UriBuilder uri = createBuilder()
						.addPath("team").addPath(create.teamId())
						.addPath("practices");
		return new CreatePracticeResponse(post(uri, create.toJSON()), create.practice());
	}
	
	public void create(final Practice.Create create, final CreatePracticeResponseHandler handler) {
		(new AsyncTask<Void, Void, CreatePracticeResponse>() {

			@Override
			protected CreatePracticeResponse doInBackground(Void... params) {
				return create(create);
			}
			
			@Override
			protected void onPostExecute(CreatePracticeResponse result) {
				handler.finish(result);
			}
			
		}).execute();
	}

	public CreatePracticesResponse create(Practice.CreateMultiple create) {
		UriBuilder uri = createBuilder()
						.addPath("team").addPath(create.teamId())
						.addPath("practices").addPath("recurring").addPath("multiple");
		return new CreatePracticesResponse(post(uri, create.toJSON()), create.practices());
	}
	
	public void create(final Practice.CreateMultiple create, final CreatePracticesResponseHandler handler) {
		(new AsyncTask<Void, Void, CreatePracticesResponse>() {

			@Override
			protected CreatePracticesResponse doInBackground(Void... params) {
				return create(create);
			}
			
			@Override
			protected void onPostExecute(CreatePracticesResponse result) {
				handler.finish(result);
			}
			
		}).execute();
	}
	
	
	
	public GetPracticeResponse get(EventBase.GetEventBase info) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(info.teamId())
							.addPath("practice").addPath(info.id())
							.addPath(info.timeZone());
		return new GetPracticeResponse(get(uri));
	}
	
	public void get(final EventBase.GetEventBase info, final GetPracticeResponseHandler handler) {
		(new AsyncTask<Void, Void, GetPracticeResponse>() {
			@Override
			protected GetPracticeResponse doInBackground(Void... params) {
				return PracticeResource.this.get(info);
			}
			
			@Override
			protected void onPostExecute(GetPracticeResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	public GetPracticesResponse getForTeam(EventBase.GetAllForTeamEventBase info) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(info.teamId())
							.addPath("practices")
							.addPath(info.timeZone())
							.addParamIf("eventType", info.eventType() != null ? info.eventType().toString() : "", info.eventType() != null);
		return new GetPracticesResponse(get(uri), info);
	}
	
	public void getForTeam(final EventBase.GetAllForTeamEventBase info, final GetPracticesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetPracticesResponse>() {
			@Override
			protected GetPracticesResponse doInBackground(Void... params) {
				return getForTeam(info);
			}
			
			@Override
			protected void onPostExecute(GetPracticesResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	public GetPracticesResponse getAll(EventBase.GetAllEventBase info, Boolean happening) {
		UriBuilder uri = createBuilder()
						.addPath("practices").addPath(info.timeZone())
						.addParamIf("happening", "now", happening)
						.addParamIf("eventType", info.eventType() != null ? info.eventType().toString() : "", info.eventType() != null);
		return new GetPracticesResponse(get(uri));
	}
	
	public void getAll(final EventBase.GetAllEventBase info, final Boolean happening, final GetPracticesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetPracticesResponse>() {
			@Override
			protected GetPracticesResponse doInBackground(Void... params) {
				return getAll(info, happening);
			}
			
			@Override
			protected void onPostExecute(GetPracticesResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	
	public UpdatePracticeResponse update(Practice.Update info) {
		UriBuilder uri = createBuilder()
						.addPath("team").addPath(info.practice().teamId())
						.addPath("practice").addPath(info.practice().practiceId());
		return new UpdatePracticeResponse(put(uri, info.toJSON()));
	}
	
	public void update(final Practice.Update info, final UpdatePracticeResponseHandler handler) {
		(new AsyncTask<Void, Void, UpdatePracticeResponse>() {
			@Override
			protected UpdatePracticeResponse doInBackground(Void... params) {
				return update(info);
			}
			
			@Override
			protected void onPostExecute(UpdatePracticeResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	
	public DeletePracticeResponse delete(Practice practice) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(practice.teamId())
							.addPath("practice").addPath(practice.practiceId());
		return new DeletePracticeResponse(delete(uri));
	}
	
	public void delete(final Practice practice, final DeletePracticeResponseHandler handler) {
		(new AsyncTask<Void, Void, DeletePracticeResponse>() {
			@Override
			protected DeletePracticeResponse doInBackground(Void... params) {
				return delete(practice);
			}
			
			@Override
			protected void onPostExecute(DeletePracticeResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
}
