package com.rteam.android.events;

import java.util.Date;

import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.events.common.UpdateLocationDialog;
import com.rteam.api.PracticeResource;
import com.rteam.api.PracticeResource.UpdatePracticeResponse;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Practice;
import com.rteam.api.business.Member.Role;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;

public class PracticeDay extends RTeamActivityChildTab implements UpdateLocationDialog.SaveLocationHandler {
	
	////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override protected String getCustomTitle() { return "rTeam - practice day"; }
	
	private TextView _lblWho;
	private TextView _lblOpponent;
	private TextView _lblTime;
	private TextView _lblLocation;
	private Button _btnUpdateLocation;
	private TextView _lblInfo;
	private TextView _lblDescription;
	
	private EventBase getEvent() { return EventDetails.getEvent(); }
	private Practice getPractice() { return (Practice) getEvent(); }
	private boolean isCoordinator() {
		return getEvent().participantRole() != null && getEvent().participantRole().atLeast(Role.Coordinator);
	}

	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows an overview of the current game."),
								new HelpContent("Location", "Shows the location that the event is currently set to."));
	}

	////////////////////////////////////////////////////////////////////
	//// Initialize
	
	@Override
	protected void initialize() {
		initializeView();
		bindView();
	}
	
	private void initializeView() {
		setContentView(R.layout.events_practiceday);
		
		_lblWho = (TextView) findViewById(R.id.lblWho);
		_lblOpponent = (TextView) findViewById(R.id.lblOpponent);
		_lblTime = (TextView) findViewById(R.id.lblTime);
		_lblLocation = (TextView) findViewById(R.id.lblLocation);
		_btnUpdateLocation = (Button) findViewById(R.id.btnUpdateLocation);
		_lblInfo = (TextView) findViewById(R.id.lblInfo);
		_lblDescription = (TextView) findViewById(R.id.lblDescription);
		
		_btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { updateLocationClicked(); }
		});
	}
	
	private void bindView() {
		bindWho();
		bindWhen();
		bindWhere();
		bindInfo();
	}
	
	private void bindWho() {
		String opponentName = getEvent().opponent();
		int visibility = StringUtils.isNullOrEmpty(opponentName) ? View.GONE : View.VISIBLE;
		
		_lblWho.setVisibility(visibility);
		_lblOpponent.setVisibility(visibility);
		_lblOpponent.setText(String.format("vs. %s", opponentName));		
	}
	
	private void bindWhen() {
		_lblTime.setText(DateUtils.toPrettyString(getEvent().startDate()));
	}
	
	private void bindWhere() {
		_lblLocation.setText(StringUtils.valueOr(getEvent().location(), "Unknown"));
		_btnUpdateLocation.setVisibility(isCoordinator() && !getEvent().startDate().before(new Date()) ? View.VISIBLE : View.GONE);
	}
	
	private void bindInfo() {
		String description = getEvent().description();
		int visibility = StringUtils.isNullOrEmpty(description) ? View.GONE : View.VISIBLE;
		
		_lblInfo.setVisibility(visibility);
		_lblDescription.setVisibility(visibility);
		_lblDescription.setText(description);
	}
	
	////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void updateLocationClicked() {
		if (isFinishing()) return;
		
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
		PracticeResource.instance().update(new Practice.Update(practice), new PracticeResource.UpdatePracticeResponseHandler() {
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
