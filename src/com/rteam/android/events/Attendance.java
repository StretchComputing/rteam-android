package com.rteam.android.events;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.events.common.AttendanceListAdapater;
import com.rteam.api.AttendanceResource;
import com.rteam.api.AttendanceResource.AttendanceResponse;
import com.rteam.api.MembersResource.MemberListResponse;
import com.rteam.api.MembersResource;
import com.rteam.api.business.Attendance.Attendee;
import com.rteam.api.business.AttendanceFilter;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Member;

public class Attendance extends RTeamActivityChildTab {
	
	////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - event attendance"; }
	
	private ListView _listAttendance;
	private ToggleButton _btnSelectAll;
	private Button _btnSave;
	
	private com.rteam.api.business.Attendance _attendance;
	private EventBase getEvent() { return EventDetails.getEvent(); }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Allows a manager/coach the ability to set the attendance for the current event."));
	}

	////////////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		loadAttendance();
	}
	
	
	private void initializeView() {
		setContentView(R.layout.events_attendance);
		
		_listAttendance = (ListView) findViewById(R.id.listAttendance);
		_btnSelectAll = (ToggleButton) findViewById(R.id.btnSelectAll);
		_btnSave = (Button) findViewById(R.id.btnSave);
		
		_btnSave.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { saveClicked(); }
		});
	}
	
	private void bindView() {
		_listAttendance.setAdapter(new AttendanceListAdapater(this, _attendance, new AttendanceListAdapater.AttendeePresentHandler() {
			@Override public void onPresentClicked(int index, long id, boolean present) { updatePresent(index, present); }
		}));
	}
	
	////////////////////////////////////////////////////////////////////////
	//// Data Loading	
	
	private void loadAttendance() {
		CustomTitle.setLoading(true, "Refreshing...");
		new AttendanceResource().get(new AttendanceFilter.EventAttendance(getEvent().eventId(), getEvent().eventType()), new AttendanceResource.AttendanceResponseHandler() {
			@Override public void finish(AttendanceResponse response) { loadAttendanceFinished(response); }
		});
	}
	
	private void loadAttendanceFinished(AttendanceResponse response) {
		if (response.showError(this)) {
			_attendance = response.getAttendance();
			
			new MembersResource().getMembers(getEvent().teamId(), new MembersResource.MemberListResponseHandler() {
				@Override
				public void finish(MemberListResponse response) { loadMembersFinished(response); }
			});
		}
		else {
			CustomTitle.setLoading(false);
		}
	}
	
	private void loadMembersFinished(MemberListResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			for(Member member : response.members()) {
				_attendance.addMemberInfo(member);
			}
			
			_attendance.addEventInfo(getEvent());

			bindView();
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void updatePresent(int index, boolean present) {
		if (_btnSelectAll.isChecked()) {
			for(Attendee a : _attendance.attendees()) {
				a.present(present);
			}
			bindView();
		}
		else {
			_attendance.attendees().get(index).present(present);
		}
		
		_btnSave.setEnabled(true);
	}
	
	private void saveClicked() {
		CustomTitle.setLoading(true, "Saving...");
		new AttendanceResource().update(_attendance, new AttendanceResource.AttendanceResponseHandler() {
			@Override public void finish(AttendanceResponse response) { saveFinished(response); }
		});
	}
	
	private void saveFinished(AttendanceResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			Toast.makeText(this, "Saved...", Toast.LENGTH_SHORT).show();
		}
		_btnSave.setEnabled(false);
	}
}
