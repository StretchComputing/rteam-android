package com.rteam.android.teams;

import android.content.Intent;
import android.widget.Toast;

import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.common.RTeamTabActivity;
import com.rteam.android.common.TabInfo;
import com.rteam.android.messaging.TwitterActivity;
import com.rteam.api.business.Team;

public class TeamDetails  extends RTeamTabActivity {
	
	private static Team _currentTeam;
	
	public static Team getTeam() { return _currentTeam; }
	public static void setTeam(Team team) {
		_currentTeam = team;
		TwitterActivity.setForTeamOnly(team);
	}
	
	@Override
	protected void initialize() {
		if (getTeam() == null) {
			finish();
			startActivity(new Intent(this, Home.class));
			Toast.makeText(this, "Missing team information, sorry for the inconvenience.", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	@Override
	protected TabInfo[] getTabs() {
		return new TabInfo[] {
			new TabInfo(TeamHome.class, "teamhome", "Team Home", R.drawable.team_tab_home),
			new TabInfo(TwitterActivity.class, "activity", "Activity", R.drawable.tab_activity),
			new TabInfo(People.class, "people", "People", R.drawable.tab_people),
			new TabInfo(Events.class, "events", "Events", R.drawable.tab_event),
			new TabInfo(Messages.class, "messages", "Messages", R.drawable.tab_messages)
		};
	}
}
