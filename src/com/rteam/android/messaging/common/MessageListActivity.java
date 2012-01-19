package com.rteam.android.messaging.common;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamListActivity;
import com.rteam.android.common.SimpleMenuItem;
import com.rteam.api.MessageThreadsResource;
import com.rteam.api.business.MessageFilters;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.common.StringUtils;
import com.rteam.android.messaging.ViewMessage;

public abstract class MessageListActivity extends RTeamListActivity implements MessageThreadsResource.GetMessagesResponseHandler {
	private ArrayList<MessageInfo> _messages = new ArrayList<MessageInfo>();
	
	private TextView _emptyText;
			
	///////////////////////////////////////////////////////////////////
	/// Super Overrides 
			
	@Override
	protected void initialize() {
		initializeView();		
		loadMessages();
		setListAdapter(new MessageInfoAdapter(this, _messages));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateListView();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ViewMessage.setMessages(_messages, position);
        startActivity(new Intent(this, ViewMessage.class));
    }
	
	@Override protected HelpProvider getHelpProvider() { return null; }
	@Override protected ArrayList<SimpleMenuItem> getSecondaryMenuItems() { return new ArrayList<SimpleMenuItem>(); }
	
	//////////////////////////////////////////////////////////////////
	/// Private Methods
	
	private void updateListView() {
		((MessageInfoAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	private void loadMessages() {		
		CustomTitle.setLoading(true, "Loading messages...");
		MessageThreadsResource.instance().getMessageThreads(getMessageFilters(), this);
	}
	
	@Override
	public void getMessageThreadsFinish(MessageThreadsResource.GetMessagesResponse response) {
		_messages.clear();
		if(addInboxMessages()) {
			_messages.addAll(cleanMessages(response.getInboxMessages()));
		}
		
		if(addOutboxMessages()) {
			_messages.addAll(cleanMessages(response.getOutboxMessages()));
		}
		
		Collections.sort(_messages);
		Collections.reverse(_messages);
				
		updateListView();
		CustomTitle.setLoading(false);
	}
	
	
	private void initializeView() {
		setContentView(R.layout.message_list);
		
		_emptyText = (TextView) findViewById(R.id.lblEmpty);
		_emptyText.setText(StringUtils.valueOr(getEmptyMessage(), "No messages were found"));
	}
	
	//////////////////////////////////////////////////////////////////
	/// Abstract Methods
	
	protected ArrayList<MessageInfo> cleanMessages(ArrayList<MessageInfo> messages) {
		return messages;
	}
	
	protected abstract MessageFilters getMessageFilters();
	
	protected abstract String getEmptyMessage();
	
	protected boolean addInboxMessages() {
		return true;
	}
	
	protected boolean addOutboxMessages() {
		return true;
	}
}
