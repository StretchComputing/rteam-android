package com.rteam.api;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.RTeamApplicationVersion;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.Attendance;
import com.rteam.api.business.AttendanceFilter;
import com.rteam.api.common.UriBuilder;

public class AttendanceResource extends ResourceBase {

	
	/////////////////////////////////////////////////////////////////////////////////
	//// .ctor
	
	public static AttendanceResource instance() {
		if (_instance == null) _instance = new AttendanceResource();
		return _instance;
	}
	
	private static AttendanceResource _instance;
	
	public AttendanceResource() {
		super(AndroidTokenStorage.get(), RTeamApplicationVersion.get());
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//// Response Classes
	
	public class AttendanceResponse extends ResourceResponse {
		private Attendance _attendance;
		public Attendance getAttendance() { return _attendance; }
		
		public AttendanceResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_attendance = new Attendance(json());
			}
		}
	}
	
	public interface AttendanceResponseHandler {
		public void finish(AttendanceResponse response);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//// Exposed Methods
	
	
	public AttendanceResponse get(AttendanceFilter filters) {
		UriBuilder uri = createBuilder()
							.addPath("attendees").addParams(filters.getParams());
		return new AttendanceResponse(get(uri));
	}
	
	public void get(final AttendanceFilter filters, final AttendanceResponseHandler handler) {
		(new AsyncTask<Void, Void, AttendanceResponse>() {

			@Override
			protected AttendanceResponse doInBackground(Void... params) {
				return AttendanceResource.this.get(filters);
			}
			
			@Override
			protected void onPostExecute(AttendanceResponse result) {
				handler.finish(result);
			}
			
		}).execute();
	}
	
	public AttendanceResponse update(Attendance attendance) {
		return new AttendanceResponse(put(createBuilder().addPath("attendees"), attendance.toJSON()));
	}
	
	public void update(final Attendance attendance, final AttendanceResponseHandler handler) {
		(new AsyncTask<Void, Void, AttendanceResponse>() {

			@Override
			protected AttendanceResponse doInBackground(Void... params) {
				return AttendanceResource.this.update(attendance);
			}
			
			@Override
			protected void onPostExecute(AttendanceResponse result) {
				handler.finish(result);
			}
			
		}).execute();
	}

}
