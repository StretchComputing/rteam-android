package com.rteam.android.common;

import java.util.Calendar;
import java.util.Date;

import com.rteam.android.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimeDialog extends Dialog {
	
	/////////////////////////////////////////////////////////////////////////
	//// Intefaces
	
	public interface DoneHandler {
		public void onDone(Date dateSelected);
	}

	/////////////////////////////////////////////////////////////////////////
	//// Members
		
	private DatePicker _date;
	private TimePicker _time;
	
	private Button _done;
	private Button _cancel;
	
	private DoneHandler _doneHandler;

	
	/////////////////////////////////////////////////////////////////////////
	//// .ctors
	
	public DateTimeDialog(Context context, DoneHandler doneHandler) {
		super(context);
		_doneHandler = doneHandler;
		initializeView();
	}
	
	public DateTimeDialog(Context context, Date defaultDate, DoneHandler doneHandler) {
		this(context, doneHandler);
		
		Calendar c = Calendar.getInstance();
		c.setTime(defaultDate);
		
		_date.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		_time.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		_time.setCurrentMinute(c.get(Calendar.MINUTE));
	}
	
	/////////////////////////////////////////////////////////////////////////
	//// Initialization
	
	private void initializeView() {
		setContentView(R.layout.dlg_datetime);
		setTitle("Choose a Date/Time: ");
		
		_date = (DatePicker) findViewById(R.id.date);
		_time = (TimePicker) findViewById(R.id.time);
		
		_done = (Button) findViewById(R.id.btnDone);
		_cancel = (Button) findViewById(R.id.btnCancel);
		
		_done.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) { doneClicked(); }
		});
		
		_cancel.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { cancelClicked(); }
		});
	}
	
	/////////////////////////////////////////////////////////////////////////
	//// Event Handlers	
	
	private void doneClicked() {
		Calendar c = Calendar.getInstance();
		c.set(_date.getYear(), _date.getMonth(), _date.getDayOfMonth(), _time.getCurrentHour(), _time.getCurrentMinute());
		
		_doneHandler.onDone(c.getTime());
		dismiss();
	}
	
	private void cancelClicked() {
		cancel();
	}

}
