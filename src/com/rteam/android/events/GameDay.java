package com.rteam.android.events;

import java.util.Date;

import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.events.common.ScoringDialog;
import com.rteam.android.events.common.UpdateLocationDialog;
import com.rteam.api.GamesResource;
import com.rteam.api.GamesResource.UpdateGameResponse;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Game;
import com.rteam.api.business.Member.Role;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;

public class GameDay extends RTeamActivityChildTab implements UpdateLocationDialog.SaveLocationHandler {
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override protected String getCustomTitle() { return "rTeam - game day"; }
	
	private TextView _lblWho;
	private TextView _lblOpponent;
	private TextView _lblTime;
	private TextView _lblLocation;
	private Button _btnUpdateLocation;
	private TextView _lblInfo;
	private TextView _lblDescription;
	
	private TextView _lblInterval;
	private TextView _lblIntervalNumber;
	
	private TextView _lblScoreUs;
	private TextView _lblScoreThem;
	private TextView _lblScoreUsName;
	private TextView _lblScoreThemName;
	
	private Button _btnUpdateScore;
	
	private EventBase getEvent() { return EventDetails.getEvent(); }
	private Game getGame() { return (Game) getEvent(); }
	private boolean isCoordinator() {
		return getEvent().participantRole() != null && getEvent().participantRole().atLeast(Role.Coordinator);
	}
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows an overview of the current game."),
								new HelpContent("Location", "Shows the location that the event is currently set to."),
								new HelpContent("Scoring", "A general way to keep the score of the game, press update score to keep track of the current score of the game."));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		if (getGame() == null) {
			Toast.makeText(this, "Unable to find game, sorry for the inconvenience.", Toast.LENGTH_SHORT).show();
			finish();
			startActivity(new Intent(this, Home.class));
		}
		
		initializeView();
		bindView();
	}
	
	
	private void initializeView() {
		setContentView(R.layout.events_gameday);
		
		_lblWho = (TextView) findViewById(R.id.lblWho);
		_lblOpponent = (TextView) findViewById(R.id.lblOpponent);
		_lblTime = (TextView) findViewById(R.id.lblTime);
		_lblLocation = (TextView) findViewById(R.id.lblLocation);
		_btnUpdateLocation = (Button) findViewById(R.id.btnUpdateLocation);
		_lblInfo = (TextView) findViewById(R.id.lblInfo);
		_lblDescription = (TextView) findViewById(R.id.lblDescription);
		
		_lblInterval = (TextView) findViewById(R.id.lblInterval);
		_lblIntervalNumber = (TextView) findViewById(R.id.lblIntervalNumber);
		
		_lblScoreUs = (TextView) findViewById(R.id.lblScoreUs);
		_lblScoreThem = (TextView) findViewById(R.id.lblScoreThem);
		_lblScoreUsName = (TextView) findViewById(R.id.lblScoreUsName);
		_lblScoreThemName = (TextView) findViewById(R.id.lblScoreThemName);
		
		_btnUpdateScore = (Button) findViewById(R.id.btnUpdateScore);
		
		_btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { updateLocationClicked(); }
		});
		
		_btnUpdateScore.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) { showScoringDialog(); }
		});
	}
	
	private void bindView() {
		bindWho();
		bindWhen();
		bindWhere();
		bindInfo();
		bindScoring();
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
	
	private void bindScoring() {
		_lblInterval.setText(String.format("%s: ", getGame().intervalName()));
		_lblIntervalNumber.setText(getGame().interval().toString());
		
		boolean notStarted = getGame().interval().isNotStarted();
		_lblScoreUs.setText(notStarted ? "N/A" : Integer.toString(getGame().scoreUs()));
		_lblScoreThem.setText(notStarted ? "N/A" : Integer.toString(getGame().scoreThem()));
		
		_lblScoreUsName.setText(StringUtils.valueOr(getEvent().teamName(), "Us"));
		_lblScoreThemName.setText(StringUtils.valueOr(getEvent().opponent(), "Opponent"));
		
		_btnUpdateScore.setVisibility(isCoordinator() ? View.VISIBLE : View.GONE);
	}
		
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void updateLocationClicked() {
		if (isFinishing()) return;
		
		new UpdateLocationDialog(this, getGame(), this).showDialog();
	}
	
	@Override
	public void saveLocation(String locationName, boolean updateAll, Location location) {
		Game game = getGame();
		game.location(locationName);
		if (location != null) {
			game.longitude(Double.toString(location.getLongitude()));
			game.latitude(Double.toString(location.getLatitude()));
		}
		
		CustomTitle.setLoading(true, "Saving...");
		GamesResource.instance().update(new Game.Update(game), new GamesResource.UpdateGameResponseHandler() {
			@Override public void finish(UpdateGameResponse response) {
				CustomTitle.setLoading(false);
				if (response.showError(GameDay.this)) {
					bindView();
				}
			}
		});
	}
	
	private void showScoringDialog() {
		if (isFinishing()) return;
		
		new ScoringDialog(this, getGame(), new ScoringDialog.ScoresUpdatedHandler() {
			@Override public void scoresUpdated() { bindScoring(); }
		}).showDialog();
	}
}
