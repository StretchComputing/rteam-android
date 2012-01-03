package com.rteam.android.messaging;

import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.messaging.common.MessageListActivity;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageFilters;

public class SentMessages extends MessageListActivity  {
	@Override
	protected String getCustomTitle() { return "rTeam - outbox"; }
	
	@Override
	protected MessageFilters getMessageFilters() {
		MessageFilters filters = new MessageFilters();
		filters.messageGroup(Message.Group.Outbox);
		return filters;
	}
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows the messages that you have sent out."));
	}
	

	@Override
	protected String getEmptyMessage() {
		return "No sent messages";
	}
}
