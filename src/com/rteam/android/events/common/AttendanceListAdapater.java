package com.rteam.android.events.common;

import com.rteam.android.R;
import com.rteam.api.business.Attendance;
import com.rteam.api.business.Attendance.Attendee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AttendanceListAdapater extends BaseAdapter {
	
	public interface AttendeePresentHandler {
		public void onPresentClicked(int index, long id, boolean present);
	}
	
	private Attendance _attendance;
	private LayoutInflater _inflater;
	private AttendeePresentHandler _presentHandler;
	
	public AttendanceListAdapater(Context context, Attendance attendance, AttendeePresentHandler presentHandler) {
		_inflater = LayoutInflater.from(context);
		_attendance = attendance;
		_presentHandler = presentHandler;
	}
	

	@Override
	public int getCount() {
		return _attendance.attendees().size();
	}

	@Override
	public Object getItem(int index) {
		return _attendance.attendees().get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup group) {
		Attendee obj = (Attendee) getItem(index);
		convertView = _inflater.inflate(R.layout.list_attendance_item, null);
		
		final int pos = index;
		
		TextView txtAttendee = (TextView) convertView.findViewById(R.id.txtAttendee);
		final ToggleButton btnPresent = (ToggleButton) convertView.findViewById(R.id.btnPresent);
		final ToggleButton btnAbsent = (ToggleButton) convertView.findViewById(R.id.btnMissing);
		
		txtAttendee.setText(obj.memberName());
		
		btnPresent.setChecked(obj.present() != null && obj.present());
		btnAbsent.setChecked(obj.present() != null && !obj.present());
		
		btnPresent.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { presentClicked(pos, btnPresent, btnAbsent, true); }
		});
		btnAbsent.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { presentClicked(pos, btnPresent, btnAbsent, false); }
		});
		
		return convertView;
	}
	
	private void presentClicked(int position, ToggleButton btnPresent, ToggleButton btnMissing, boolean present) {
		btnPresent.setChecked(present);
		btnMissing.setChecked(!present);
		_presentHandler.onPresentClicked(position, getItemId(position), present);
	}
}
