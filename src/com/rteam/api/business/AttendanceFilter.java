package com.rteam.api.business;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;

import com.rteam.api.common.DateUtils;
import com.rteam.api.common.TimeZoneUtils;

public abstract class AttendanceFilter {
	
	private Date _startDate;
	public Date startDate() { return _startDate; }
	public void startDate(Date value) { _startDate = value; }
	
	private Date _endDate;
	public Date endDate() { return _endDate; }
	public void endDate(Date value) { _endDate = value; }
	
	protected AttendanceFilter() {}
	protected AttendanceFilter(Date startDate, Date endDate) {
		_startDate = startDate;
		_endDate = endDate;
	}
	
	
	public ArrayList<BasicNameValuePair> getParams() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		
		params.addAll(getParamsInner());
		if (endDate() != null) params.add(new BasicNameValuePair("endDate", DateUtils.toDateParameterString(endDate())));
		if (startDate() != null) params.add(new BasicNameValuePair("startDate", DateUtils.toDateParameterString(startDate())));
		
		return params;
	}
	
	protected abstract ArrayList<BasicNameValuePair> getParamsInner();

	
	public static class TeamAttendance extends AttendanceFilter {
		private String _teamId;
		public String teamId() { return _teamId; }
		public void teamId(String value) { _teamId = value; }
		
		public TeamAttendance(String teamId) {
			_teamId = teamId;
		}
		public TeamAttendance(String teamId, Date startDate, Date endDate) {
			super(startDate, endDate);
			_teamId = teamId;
		}
		
		protected ArrayList<BasicNameValuePair> getParamsInner() {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("teamId", teamId()));
			return params;
		}
	}
	
	public static class EventAttendance extends AttendanceFilter {
		private String _eventId;
		public String eventId() { return _eventId; }
		public void eventId(String value) { _eventId = value; }
		
		private Event.Type _eventType;
		public Event.Type eventType() { return _eventType; }
		public void eventType(Event.Type value) { _eventType = value; }
		
		
		public EventAttendance(String eventId, Event.Type eventType) {
			_eventId = eventId;
			_eventType = eventType;
		}
		
		protected ArrayList<BasicNameValuePair> getParamsInner() {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("eventId", eventId()));
			params.add(new BasicNameValuePair("eventType", eventType().toString()));
			return params;
		}
	}
	
	public static class MemberAttendance extends AttendanceFilter {
		private String _memberId;
		public String memberId() { return _memberId; }
		public void memberId(String value) { _memberId = value; }
		
		private String _timeZone;
		public String timeZone() { return _timeZone; }
		public void timeZone(String value) { _timeZone = value; }
		
		public MemberAttendance(String memberId) { this(memberId, TimeZoneUtils.getTimeZone()); }
		public MemberAttendance(String memberId, String timeZone) {
			_memberId = memberId;
			_timeZone = timeZone;
		}
		public MemberAttendance(String memberId, Date startDate, Date endDate) { this(memberId, TimeZoneUtils.getTimeZone(), startDate, endDate); }
		public MemberAttendance(String memberId, String timeZone, Date startDate, Date endDate) {
			super(startDate, endDate);
			_memberId = memberId;
			_timeZone = timeZone;
		}
		
		protected ArrayList<BasicNameValuePair> getParamsInner() {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("memberId", memberId()));
			params.add(new BasicNameValuePair("timeZone", timeZone()));
			return params;
		}
	}
}
