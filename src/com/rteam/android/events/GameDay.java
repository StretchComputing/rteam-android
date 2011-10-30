package com.rteam.android.events;

import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class GameDay extends RTeamActivityChildTab implements UpdateLocationDialog.SaveLocationHandler {
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override protected String getCustomTitle() { return "rTeam - game day"; }
	
	private TextView _txtTypeVsOpponent;
	private TextView _txtStartDate;
	
	private EditText _txtLocation;
	
	private TextView _lblScoreSummary;
	private Button _btnUpdateScore;
	
	
	private EventBase getEvent() { return EventDetails.getEvent(); }
	private Game getGame() { return (Game) getEvent(); }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows an overview of the current event."),
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
		
		_txtTypeVsOpponent = (TextView) findViewById(R.id.txtTypeVsOpponent);
		_txtStartDate = (TextView) findViewById(R.id.txtStartDate);
		
		_txtLocation = (EditText) findViewById(R.id.txtLocation);

		_lblScoreSummary = (TextView) findViewById(R.id.lblScoreSummary);
		_btnUpdateScore = (Button) findViewById(R.id.btnUpdateScore);
		
		_txtLocation.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) { updateLocationClicked(); }
		});
		
		_btnUpdateScore.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) { showScoringDialog(); }
		});
	}
	
	private void bindView() {
		_btnUpdateScore.setVisibility((getEvent().participantRole() != null && getEvent().participantRole().atLeast(Role.Coordinator)) ? View.VISIBLE : View.GONE);
		_txtTypeVsOpponent.setText(String.format("%s vs. %s", 
				getEvent().eventType() != null ? getEvent().eventType().toPrettyString() : "Event",
				getEvent().opponent()));
		_txtStartDate.setText(DateUtils.toPrettyString(getEvent().startDate()));
		_txtLocation.setText(getEvent().location());
		bindScoring();
	}
	
	private void bindScoring() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Interval: %s\n", getGame().interval().toString()));
		if (getGame().interval().isGameOver())   sb.append(String.format("Final Score: Us=%d  Them=%d\n", getGame().scoreUs(), getGame().scoreThem()));
		if (getGame().interval().isInProgress()) sb.append(String.format("Current Score: Us=%d  Them=%d\n", getGame().scoreUs(), getGame().scoreThem()));

		_lblScoreSummary.setText(sb.toString());
	}
		
	////////////////////////////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private void updateLocationClicked() {
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
