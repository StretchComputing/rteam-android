package com.rteam.android.events;

import java.util.ArrayList;

import com.rteam.android.R;
import com.rteam.android.common.RTeamTabActivity;
import com.rteam.android.common.TabInfo;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Member.Role;
import com.rteam.api.business.Team;

public class EventDetails extends RTeamTabActivity {
	
	private static EventBase _event;
	public static EventBase getEvent() { return _event; }	
	private static Team _team;
	public static Team getTeam() { return _team; }
	
	public static void setup(EventBase event, Team team) {
		_event = event;
		_team = team;
	}
	
	@Override
	protected TabInfo[] getTabs() {
		boolean isGame = getEvent().eventType() == Event.Type.Game;
		ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();
		
		if (isGame) tabs.add(new TabInfo(GameDay.class, "GameDay", "Game Day", R.drawable.events_tab_gameday));
		else tabs.add(new TabInfo(PracticeDay.class, "PracticeDay", "Practice Day", R.drawable.events_tab_practiceday));
		
		tabs.add(new TabInfo(Activity.class, "Activity", "Activity", R.drawable.tab_activity));
		if (getEvent().participantRole() != null && getEvent().participantRole() == Role.Coordinator) {
			tabs.add(new TabInfo(Attendance.class, "Attendance", "Attendance", R.drawable.events_tab_attendance));
		}
		if (isGame) tabs.add(new TabInfo(Messages.class, "Messages", "Messages", R.drawable.tab_messages));
		
		return tabs.toArray(new TabInfo[tabs.size()]);
	}

}
