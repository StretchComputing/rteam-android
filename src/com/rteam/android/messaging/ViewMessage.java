package com.rteam.android.messaging;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.FlowLayout;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.api.MessageThreadsResource;
import com.rteam.api.MessageThreadsResource.GetMessageResponse;
import com.rteam.api.MessageThreadsResource.UpdateMessageResponse;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.business.UpdateMessageInfo;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;

public class ViewMessage extends RTeamActivity implements View.OnClickListener {
	
	///////////////////////////////////////////////////////////////////
	//// Static Members
	
	private static ArrayList<MessageInfo> _messages;
	private static int _selectedIndex;
			
	public static void setMessages(ArrayList<MessageInfo> messages, int selectedIndex) { 
		_messages = messages;
		_selectedIndex = selectedIndex;
	}
	
	////////////////////////////////////////////////////////////////////
	//// Members
			
	private TextView _messageType;
	private TextView _subject;
	private TextView _sender;
	private TextView _timestamp;
	private TextView _teamSummary;
	private TextView _body;
	
	private ImageButton _btnPrevious;
	private ImageButton _btnNext;
	
	private TextView _pollMessage;
	private FlowLayout _pollChoices;
	private TextView _textResponse;
	
	private View _followupSeparator;
	private TextView _followup;
	
	@Override protected HelpProvider getHelpProvider() { return new HelpProvider(new HelpContent("Overview", "Viewing a message.")); }
	@Override protected String getCustomTitle() { return "rTeam - view message"; }
			
	
	private MessageInfo getSelectedMessage() {
		if (_selectedIndex >= 0 && _selectedIndex < _messages.size()) {
			return _messages.get(_selectedIndex);
		}
		return null;
	}
					
	@Override
	protected void initialize() {
		final MessageInfo message = getSelectedMessage();
		if (message != null) {
			CustomTitle.setLoading(true, "Loading message...");
			MessageThreadsResource.instance().getMessageThread(message.teamId(), message.messageThreadId(), true, new MessageThreadsResource.GetMessageResponseHandler() {				
				@Override 
				public void finish(GetMessageResponse response) {
					if (response.showError(ViewMessage.this)) {
						message.merge(response.getMessage());
					}
					doneLoadingMessageInfo();
				}
			});
		}
		else {
			doneLoadingMessageInfo();
		}
	}
	
	private void doneLoadingMessageInfo() {
		MessageInfo message = getSelectedMessage();
		
		if (message == null) {
			TextView textMsg = new TextView(this);
			textMsg.setText("Error, message info is not set.");
			setContentView(textMsg);
		} else {
			setContentView(R.layout.message_info);
			
			initializeView();
			initializeMessage(message);
		}
	}
	
	private void initializeView() {
		_messageType = (TextView) findViewById(R.id.textMessageType);
		_sender = (TextView) findViewById(R.id.textMessageSender);
		_subject = (TextView) findViewById(R.id.textSubject);
		_timestamp = (TextView) findViewById(R.id.textMessageTimestamp);
		_teamSummary = (TextView) findViewById(R.id.textTeamSummary);
		_body = (TextView) findViewById(R.id.textMessageBody);
		
		_btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		_btnNext = (ImageButton) findViewById(R.id.btnNext);
		
		_pollMessage = (TextView) findViewById(R.id.textPollChoicesMessage);
		_pollChoices = (FlowLayout) findViewById(R.id.pollChoices);
		_textResponse = (TextView) findViewById(R.id.textResponse);
		
		_followupSeparator = findViewById(R.id.followupSeparator);
		_followup = (TextView) findViewById(R.id.textFollowup);
		
		_btnPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { onPreviousClicked(); }
		});
		_btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { onNextClicked(); }
		});
	}
	
	private void onPreviousClicked() {
		_selectedIndex--;
		initialize();
	}
	private void onNextClicked() {
		_selectedIndex++;
		initialize();
	}
	
	
	private void initializeMessage(MessageInfo message) {
		bindView(message);
		updateMessageViewed(message);
	}
	
	private void bindView(MessageInfo message) {
		String messageInfo = message.type().toStringPretty() + " "
								+ (_selectedIndex + 1) + " of " + _messages.size();
		
		_messageType.setText(messageInfo);
		_sender.setText("From: " + StringUtils.valueOr(message.senderName(), "rTeam"));
		_subject.setText(message.subject());
		_timestamp.setText(DateUtils.toPrettyString(message.receivedDate()));
		_teamSummary.setText("Team: " + message.teamName());
		_body.setText(message.body());
		
		_btnPrevious.setEnabled(_selectedIndex != 0);
		_btnNext.setEnabled(_selectedIndex != _messages.size() - 1);
		
		int visible = (message.type() == Message.Type.Poll) ? View.VISIBLE : View.INVISIBLE;
		_pollMessage.setText("");
		_pollChoices.removeAllViews();
		
		_pollMessage.setVisibility(visible);
		_pollChoices.setVisibility(visible);
		_textResponse.setVisibility(View.INVISIBLE);
		if (message.type() == Message.Type.Poll) {
			showPoll(message);
		}
		
		 visible = message.hasFollowup() ? View.VISIBLE : View.INVISIBLE;
		_followupSeparator.setVisibility(visible);
		_followup.setVisibility(visible);
		_followup.setText(message.followUpMessage());
	}
	
	private void showPoll(MessageInfo message) {
		if (message.isPollCompleted()) {
			showPollSummary(message);
		}
		else if(message.hasReplied()) {
			showPollResponse(message);
		}
		else {
			showPollChoices(message);
		}
	}
	
	private void showPollResponse(MessageInfo message) {
		_pollMessage.setText("Your response: " + message.getReply());
	}
	
	private void showPollChoices(MessageInfo message) {
		if (message.pollChoices() != null && message.pollChoices().size() > 0) {
			_pollMessage.setText("Please choose an option to respond: ");
			for(String option : message.pollChoices()) {
				Button optionButton = new Button(this);
				optionButton.setText(option);
				optionButton.setOnClickListener(this);
				
				_pollChoices.addView(optionButton);
			}
		}
	}
	
	private void showPollSummary(MessageInfo message) {
		_pollMessage.setText("The poll has been marked as completed by the creator. Poll results:");
		
		HashMap<String, Integer> responses = message.getPollResponses();
		for(String choice : responses.keySet()) {
			TextView view = new TextView(this);
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			view.setText(choice + " : " + responses.get(choice));
			_pollChoices.addView(view);
		}
		
		_textResponse.setVisibility(View.VISIBLE);
		_textResponse.setText((message.hasReplied())
							? "Your response: " + message.getReply()
							: "You didn't response.");
	}
			
	@Override
	public void onClick(View buttonView) {
		if (buttonView != null && (buttonView instanceof Button)) {
			Button btn = (Button) buttonView;
			respondToMessage(getSelectedMessage(), btn.getText().toString());
		}
	}
	
	private void respondToMessage(final MessageInfo message, String response) {
		if (!message.hasReplied()) {
			message.setReply(response);
			CustomTitle.setLoading(true, "Sending response...");
			MessageThreadsResource.instance().updateMessageThread(new UpdateMessageInfo.Reply(message, response), new MessageThreadsResource.UpdateMessageResponseHandler() {
				@Override
				public void finish(UpdateMessageResponse response) {
					CustomTitle.setLoading(false);
					initializeMessage(message);
				}
			});
		}
	}
	
	private void updateMessageViewed(MessageInfo message) {
		if(!message.wasViewed()) {
			message.wasViewed(true);
			
			CustomTitle.setLoading(true);
			MessageThreadsResource.instance().updateMessageThread(new UpdateMessageInfo.Viewed(message), new MessageThreadsResource.UpdateMessageResponseHandler() {
				@Override public void finish(UpdateMessageResponse response) { 
					CustomTitle.setLoading(false);
				}
			});
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_info, menu);
        return true;
    }
	
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:  		menuBackPressed(); return true; 
		case R.id.menu_delete: 		menuDeletePressed(); return true;
		case R.id.menu_reply:   	menuReplyPressed(); return true;
		case R.id.menu_markunread: 	menuMarkUnreadPressed(); return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	private void menuBackPressed() {
		finish();
	}
	
	private void menuDeletePressed() {
		// Remove the message from the list
		final MessageInfo message = _messages.remove(_selectedIndex);
		
		// Push the change to the server
		CustomTitle.setLoading(true, "Deleting...");
		MessageThreadsResource.instance().updateMessageThread(new UpdateMessageInfo.Delete(message), new MessageThreadsResource.UpdateMessageResponseHandler() {
			@Override public void finish(UpdateMessageResponse response) { messageDeleteFinished(message); }
		});
	}
	
	private void messageDeleteFinished(MessageInfo message) {
		toastAndFinish(String.format("'%s' has been deleted.", message.subject()));
	}
	
	private void menuReplyPressed() {
		SendMessage.setupReply(getSelectedMessage());
		startActivity(new Intent(this, SendMessage.class));
	}
	
	private void menuMarkUnreadPressed() {
		final MessageInfo message = getSelectedMessage();
		message.wasViewed(false);
		
		MessageThreadsResource.instance().updateMessageThread(new UpdateMessageInfo.Viewed(message), new MessageThreadsResource.UpdateMessageResponseHandler() {
			@Override public void finish(UpdateMessageResponse response) { markUnreadFinished(message); } 
		});
		
	}
	
	private void markUnreadFinished(MessageInfo message) {
		toastAndFinish(String.format("'%s' marked as unread.", message.subject()));
	}
	
	private void toastAndFinish(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
		
		finish();
	}
}
