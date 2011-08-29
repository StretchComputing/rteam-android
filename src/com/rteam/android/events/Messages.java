package com.rteam.android.events;

import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.messaging.common.MessagesFor;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.MessageFilters;
import com.rteam.api.business.Team;
import com.rteam.api.common.TimeZoneUtils;

public class Messages extends MessagesFor {
	
	@Override
	protected String getCustomTitle() { return "rTeam - event messages"; }
	
	@Override
	protected MessageFilters getMessageFilters() {
		MessageFilters filters = new MessageFilters();
		
		filters.eventId(getEvent().eventId());
		filters.eventType(getEvent().eventType());
		filters.timeZone(TimeZoneUtils.getTimeZone());
		
		return filters;
	}

	@Override
	protected Team getTeam() {
		return EventDetails.getTeam();
	}

	@Override
	protected EventBase getEvent() {
		return EventDetails.getEvent();
	}

	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows the messages for the current game."));
	}
}
