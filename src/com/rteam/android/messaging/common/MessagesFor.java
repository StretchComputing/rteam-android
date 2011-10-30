package com.rteam.android.messaging.common;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.messaging.CreatePoll;
import com.rteam.android.messaging.SendMessage;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.MessageThreadsResource;
import com.rteam.api.MessageThreadsResource.GetMessagesResponse;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Member.Role;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageFilters;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.business.Team;

public abstract class MessagesFor extends RTeamActivityChildTab implements MessageThreadsResource.GetMessagesResponseHandler {

	//////////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	private ExpandableListView _listMessages;
	private Button _btnDelete;
	private Button _btnSend;
	
	private ArrayList<MessageInfo> _selectedMessages = new ArrayList<MessageInfo>();
	
	private ArrayList<MessageInfo> _inbox;
	private ArrayList<MessageInfo> _outbox;
	
	protected abstract MessageFilters getMessageFilters();
	
	@Override
	protected HelpProvider getHelpProvider() { return null; }
	
	//////////////////////////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		loadMessages();
	}
	
	@Override
	protected void reInitialize() {
		loadMessages();
	}
	
	private void initializeView() {
		setContentView(R.layout.message_all_for);
		
		_listMessages = (ExpandableListView) findViewById(R.id.listMessages);
		_btnDelete = (Button) findViewById(R.id.btnDelete);
		_btnSend = (Button) findViewById(R.id.btnSend);
		
		_btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { deleteClicked(); }
		});
		
		_btnSend.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { sendClicked(); }
		});
	}
	
	private void bindView() {
		_listMessages.setAdapter(new MessagesForListAdapter(this, _inbox, _outbox, new MessagesForListAdapter.MessageCheckedHandler() {
			@Override
			public void onMessageChecked(MessageInfo message, boolean checked) {
				if (checked) _selectedMessages.add(message);
				else _selectedMessages.remove(message);
				bindDeleted();
			}
		}));
		
		_listMessages.expandGroup(MessagesForListAdapter.InboxPosition);
		_listMessages.expandGroup(MessagesForListAdapter.OutboxPosition);
		
		bindDeleted();
	}
	
	private void bindDeleted() {
		int numberMessagesSelected = 0;
		for (MessageInfo message : _selectedMessages) {
			Team messageTeam = TeamCache.get(message.teamId());
			if (messageTeam != null && messageTeam.participantRole().atLeast(Role.Coordinator) || getTokenStorage().getUserToken().equals(message.senderMemberId())) {
				numberMessagesSelected++;
			}
		}
		_btnDelete.setEnabled(numberMessagesSelected > 0);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//// Data Loading
	
	private void loadMessages() {
		CustomTitle.setLoading(true, "Loading messages...");
		MessageThreadsResource.instance().getMessageThreads(getMessageFilters(), this);
	}
	
	@Override
	public void getMessageThreadsFinish(GetMessagesResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_inbox = response.getInboxMessages();
			_outbox = response.getOutboxMessages();
			
			bindView();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	//// Event Handling 
	
	private void deleteClicked() {
		if (_selectedMessages.size() == 0) return;
	
		ArrayList<MessageInfo> fromInbox = new ArrayList<MessageInfo>();
		ArrayList<MessageInfo> fromOutbox = new ArrayList<MessageInfo>();
		
		for (MessageInfo msg : _selectedMessages) {
			if (_inbox.contains(msg)) {
				fromInbox.add(msg);
				_inbox.remove(msg);
			}
			else {
				fromOutbox.add(msg);
				_outbox.remove(msg);
			}
		}
		
		MessageThreadsResource.instance().archive(fromInbox, Message.Group.Inbox, null); 
		MessageThreadsResource.instance().archive(fromOutbox, Message.Group.Outbox, null);
		
		_selectedMessages.clear();
		bindView();
	}
	
	private void sendClicked() {
		if (isFinishing()) return;
		
		new AlertDialog.Builder(this)
				.setTitle("What type of message?")
				.setItems(new String[] { "Message", "Poll" }, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0) sendMessage();
						else if(which == 1) sendPoll();
					}
				})
				.show();
	}
	
	protected abstract Team getTeam();
	protected abstract EventBase getEvent();
	
	protected void sendMessage() {
		SendMessage.clearOnSend(false);
		SendMessage.setupTeam(getTeam());
		SendMessage.setupEvent(getEvent()); // TODO : Do we want to have a handler or something to come BACK to this page after sending a message?
		
		startActivity(new Intent(this, SendMessage.class));
	}
	protected void sendPoll() {
		CreatePoll.clearOnSend(false);
		CreatePoll.setupTeam(getTeam());
		CreatePoll.setupEvent(getEvent());
		
		startActivity(new Intent(this, CreatePoll.class));
	}
}
