package com.rteam.api;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.Activity;
import com.rteam.api.business.Activity.ActivityFilter;
import com.rteam.api.common.BitmapUtils;
import com.rteam.api.common.UriBuilder;

public class ActivitiesResource extends ResourceBase {
	
	/////////////////////////////////////////////////////////////////////////////////
	///// .ctor

	public ActivitiesResource() {
		super(AndroidTokenStorage.get());
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///// Response Classes
	
	public class GetActivitiesResponse extends ResourceResponse {

		private ArrayList<Activity> _activities;
		public ArrayList<Activity> activities() { return _activities; }
		
		protected GetActivitiesResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				JSONArray activities = json().optJSONArray("activities");
				_activities = new ArrayList<Activity>();
				
				if (activities != null) {
					for(int i=0; i<activities.length(); i++) {
						_activities.add(new Activity(activities.optJSONObject(i)));
					}
				}
			}
		}
	}
	
	public interface GetActivitiesResponseHandler {
		public void finish(GetActivitiesResponse response);
	}
	
	public class CreateActivityResponse extends ResourceResponse {
		protected CreateActivityResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface CreateActivityResponseHandler {
		public void finish(CreateActivityResponse response);
	}
	
	public class GetActivityPhotoResponse extends ResourceResponse {
		protected GetActivityPhotoResponse(APIResponse response, Activity activity) {
			super(response);
			initialize(activity);
		}
		
		private void initialize(Activity activity) {
			if (isResponseGood()) {
				activity.photo(BitmapUtils.getBitmapFrom(json().optString("photo")));
			}
		}
	}
	
	public interface GetActivityPhotoResponseHandler {
		public void finish(GetActivityPhotoResponse response);
	}
	
	public class GetActivityVideoResponse extends ResourceResponse {		
		protected GetActivityVideoResponse(APIResponse response, Activity activity) {
			super(response);
			initialize(activity);
		}
		
		private void initialize(Activity activity) {
			if (isResponseGood()) {
				activity.rawVideo(json().optString("video"));
			}
		}
	}
	
	public interface GetActivityVideoResponseHandler {
		public void finish(GetActivityVideoResponse response);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	///// Exposed Methods

	public GetActivitiesResponse getActivities(ActivityFilter filters) {
		UriBuilder uri = createBuilder()
							.addPathIf("team", filters.hasTeamId()).addPathIf(filters.teamId(), filters.hasTeamId())
							.addPath("activities").addPath(filters.timeZone())
							.addParams(filters.toParams());
		return new GetActivitiesResponse(get(uri));
	}
	
	public void getActivities(final ActivityFilter filters, final GetActivitiesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetActivitiesResponse>() {

			@Override
			protected GetActivitiesResponse doInBackground(Void... arg0) {
				return getActivities(filters);
			}
			
			@Override
			protected void onPostExecute(GetActivitiesResponse result) {
				handler.finish(result);
			}
			
		}).execute();
	}

	public CreateActivityResponse create(Activity activity) {
		UriBuilder uri = createBuilder().addPath("team").addPath(activity.teamId()).addPath("activities");
		return new CreateActivityResponse(post(uri, activity.toJSONCreate()));
	}
	
	public void create(final Activity activity, final CreateActivityResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateActivityResponse>() {
			
			@Override
			protected CreateActivityResponse doInBackground(Void... arg0) {
				return create(activity);
			}
			
			@Override
			protected void onPostExecute(CreateActivityResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	
	public GetActivityPhotoResponse getPhoto(Activity activity) {
		return new GetActivityPhotoResponse(get(createBuilder()
													.addPath("team").addPath(activity.teamId())
													.addPath("activity").addPath(activity.activityId())
													.addPath("photo")), activity);
	}
	
	public void getPhoto(final Activity activity, final GetActivityPhotoResponseHandler handler) {
		(new AsyncTask<Void, Void, GetActivityPhotoResponse>() {
			
			@Override
			protected GetActivityPhotoResponse doInBackground(Void... arg0) {
				return getPhoto(activity);
			}
			
			@Override
			protected void onPostExecute(GetActivityPhotoResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	public GetActivityVideoResponse getVideo(Activity activity) {
		return new GetActivityVideoResponse(get(createBuilder()
													.addPath("team").addPath(activity.teamId())
													.addPath("activity").addPath(activity.activityId())
													.addPath("video")), activity);
	}
	
	public void getVideo(final Activity activity, final GetActivityVideoResponseHandler handler) {
		(new AsyncTask<Void, Void, GetActivityVideoResponse>() {
			
			@Override
			protected GetActivityVideoResponse doInBackground(Void... arg0) {
				return getVideo(activity);
			}
			
			@Override
			protected void onPostExecute(GetActivityVideoResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
}
