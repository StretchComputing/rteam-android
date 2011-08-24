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
import com.rteam.api.business.MessageInfo;

public class Polls extends MessageListActivity {
	@Override
	protected String getCustomTitle() { return "rTeam - polls"; }
	
	@Override
	protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() {
		ArrayList<SimpleMenuItem> items = new ArrayList<SimpleMenuItem>();
		items.add(new SimpleMenuItem("Create Poll", new MenuItem.OnMenuItemClickListener() {
				@Override public boolean onMenuItemClick(MenuItem item) { createPollClicked(); return true; } }));		
		return items;
	}
	
	private void createPollClicked() {
		startActivity(new Intent(this, CreatePoll.class));
	}
	
	@Override
	protected MessageFilters getMessageFilters() {
		MessageFilters filters = new MessageFilters();
		filters.messageGroup(Message.Group.Outbox);
		return filters;
	}
	
	@Override
	protected ArrayList<MessageInfo> cleanMessages(ArrayList<MessageInfo> messages) {
		ArrayList<MessageInfo> polls = new ArrayList<MessageInfo>();
		for(MessageInfo message : messages) {
			if (message.type() == Message.Type.Poll) {
				polls.add(message);
			}
		}
		return polls;
	}
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows polls that you have sent out."));
	}
}