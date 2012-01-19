package com.rteam.android.messaging;

import java.util.ArrayList;

import android.content.Intent;
import android.view.MenuItem;

import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.common.SimpleMenuItem;
import com.rteam.android.messaging.common.MessageListActivity;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageFilters;

public class Inbox extends MessageListActivity {	
	/////////////////////////////////////////////////////////////
	//// Members
	
	@Override protected String getCustomTitle() { return "rTeam - inbox"; }
	@Override protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() {
		ArrayList<SimpleMenuItem> items = new ArrayList<SimpleMenuItem>();
		items.add(new SimpleMenuItem("Send Message", new MenuItem.OnMenuItemClickListener() {
			@Override public boolean onMenuItemClick(MenuItem item) { clickSendMessage(); return true; } }));
		return items;
	}
	
	@Override
	protected MessageFilters getMessageFilters() {
		MessageFilters filters = new MessageFilters();
		filters.messageGroup(Message.Group.Inbox);
		return filters;
	}
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows the current messages for all teams."));
	}

	@Override
	protected String getEmptyMessage() {
		return "No messages found";
	}
	
	private void clickSendMessage() {
		startActivity(new Intent(this, SendMessage.class));
	}
	
	@Override 
	protected boolean addOutboxMessages() { 
		return false;
	}
}
