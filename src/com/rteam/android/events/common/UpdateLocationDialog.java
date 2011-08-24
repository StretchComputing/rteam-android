package com.rteam.android.events.common;

import com.rteam.android.R;
import com.rteam.android.common.MyLocation;
import com.rteam.api.business.EventBase;
import com.rteam.api.common.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateLocationDialog {
	
	public interface SaveLocationHandler {
		public void saveLocation(String locationName, boolean updateAll, Location gpsCoordinates);
	}
	
	private SaveLocationHandler _saveHandler;

	private View _view;
	
	private EventBase _event;
	
	private EditText _txtName;
	private CheckBox _chkUpdateForAllGames;
	private Button _btnSetCoordinates;
	private TextView _lblGPSLabel;
	private TextView _lblGPSCoordinates;
	
	private Context _context;
	
	private Location _location;
	
	public UpdateLocationDialog(Context context, EventBase event, SaveLocationHandler saveHandler) {
		_context = context;
		_event = event;
		_saveHandler = saveHandler;
		initializeView();
	}
	
	
	private void initializeView() {
		_view = LayoutInflater.from(_context).inflate(R.layout.dlg_updatelocation, null);
		
		_txtName = (EditText) _view.findViewById(R.id.txtName);
		_chkUpdateForAllGames = (CheckBox) _view.findViewById(R.id.chkUpdateAll);
		_btnSetCoordinates = (Button) _view.findViewById(R.id.btnSetCoordinates);
		_lblGPSLabel = (TextView) _view.findViewById(R.id.lblGPSLabel);
		_lblGPSCoordinates = (TextView) _view.findViewById(R.id.lblGPSCoordinates);
		
		_btnSetCoordinates.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { trySetCoordinates(); }
		});
		
		bindView();
	}
	
	private void bindView() {
		_txtName.setText(_event.location());
		
		if (!StringUtils.isNullOrEmpty(_event.longitude()) && !StringUtils.isNullOrEmpty(_event.latitude())) {
			_lblGPSLabel.setText(String.format("Previous Location: %s,%s", _event.longitude(), _event.latitude()));
			_lblGPSLabel.setVisibility(View.VISIBLE);
		}
	}
	
	
	public void showDialog() {
		new AlertDialog.Builder(_context)
				.setTitle("Update Location")
				.setView(_view)
				.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { save(); } })
				.show();
	}	
	
	private void trySetCoordinates() {
		MyLocation loc = new MyLocation();
		if (!loc.getLocation(_context, new MyLocation.LocationResult(){ @Override public void gotLocation(Location location) { setLocation(location); } })) {
			notifyLocationError();
		}
	}
	
	private void notifyLocationError() {
		Toast.makeText(_context, "Unable to obtain location...", Toast.LENGTH_SHORT).show();
	}
	
	private void setLocation(Location location) {
		if (location == null) { notifyLocationError(); return; }
		_location = location;
		
		_btnSetCoordinates.setVisibility(View.GONE);
		_lblGPSCoordinates.setVisibility(View.GONE);
		_lblGPSLabel.setVisibility(View.VISIBLE);
		
		_lblGPSLabel.setText(String.format("%d,%d", _location.getLongitude(), _location.getLatitude()));
	}
	
	private void save() {
		_saveHandler.saveLocation(_txtName.getText().toString(), _chkUpdateForAllGames.isChecked(), _location);
	}
}
