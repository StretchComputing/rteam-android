package com.rteam.android.events;

import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.events.common.UpdateLocationDialog;
import com.rteam.api.PracticeResource;
import com.rteam.api.PracticeResource.UpdatePracticeResponse;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Practice;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;

public class PracticeDay extends RTeamActivityChildTab implements UpdateLocationDialog.SaveLocationHandler {
	
	////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override protected String getCustomTitle() { return "rTeam - practice day"; }
	
	private TextView _lblTime;
	private TextView _lblLocation;
	private Button _btnUpdateLocation;	
	private TextView _lblDescription;

	private EventBase getEvent() { return EventDetails.getEvent(); }
	private Practice getPractice() { return (Practice) getEvent(); }


	////////////////////////////////////////////////////////////////////
	//// Initialize
	
	@Override
	protected void initialize() {
		initializeView();
		bindView();
	}
	
	private void initializeView() {
		setContentView(R.layout.events_practiceday);
		
		_lblTime = (TextView) findViewById(R.id.lblTime);
		_lblLocation = (TextView) findViewById(R.id.lblLocation);
		_btnUpdateLocation = (Button) findViewById(R.id.btnUpdateLocation);
		_lblDescription = (TextView) findViewById(R.id.lblDescription);
		
		_btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { updateLocationClicked(); }
		});
	}
	
	private void bindView() {
		_lblTime.setText(DateUtils.toPrettyString(EventDetails.getEvent().startDate()));
		_lblLocation.setText(EventDetails.getEvent().location());
		_lblDescription.setText(StringUtils.isNullOrEmpty(EventDetails.getEvent().description())
									? "No description entered..."
									: EventDetails.getEvent().description());
	}
	
	////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void updateLocationClicked() {
		new UpdateLocationDialog(this, getPractice(), this).showDialog();
	}
	
	@Override
	public void saveLocation(String locationName, boolean updateAll, Location location) {
		Practice practice = getPractice();
		practice.location(locationName);
		if (location != null) {
			practice.longitude(Double.toString(location.getLongitude()));
			practice.latitude(Double.toString(location.getLatitude()));
		}
		CustomTitle.setLoading(true, "Saving...");
		new PracticeResource().update(new Practice.Update(practice), new PracticeResource.UpdatePracticeResponseHandler() {
			@Override 
			public void finish(UpdatePracticeResponse response) {
				CustomTitle.setLoading(false);
				if (response.showError(PracticeDay.this)) {
					bindView();
				}				
			}
		});
	}

}
