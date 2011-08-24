package com.rteam.android.teams;

import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.messaging.common.MessagesFor;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.MessageFilters;
import com.rteam.api.business.Team;
import com.rteam.api.common.TimeZoneUtils;

public class Messages extends MessagesFor {

	@Override
	protected String getCustomTitle() { return "rTeam - team messages"; }
	
	@Override
	protected MessageFilters getMessageFilters() {
		MessageFilters filters = new MessageFilters();
		
		filters.teamId(TeamDetails.getTeam().teamId());
		filters.timeZone(TimeZoneUtils.getTimeZone());
		
		return filters;
	}
	
	@Override
	protected Team getTeam() { return TeamDetails.getTeam(); }
	
	@Override
	protected EventBase getEvent() { return null; }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows the messages for the current team."));
	}
}