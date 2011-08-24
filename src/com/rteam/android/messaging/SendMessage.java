package com.rteam.android.messaging;

import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rteam.android.R;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.messaging.common.CreateMessageBase;
import com.rteam.api.business.Member;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.business.NewMessageInfo;
import com.rteam.api.common.ArrayListUtils;
import com.rteam.api.common.ArrayListUtils.GetString;

public class SendMessage extends CreateMessageBase {

	//////////////////////////////////////////////////////////////////////////
	//// Setup for Reply
	
	private static ArrayList<Member> _recipientList = new ArrayList<Member>();
	private static String _subjectIn = "";
	private static MessageInfo _replyToMessage;
	
	public static void setupReply(MessageInfo message) {
		_subjectIn = "RE: " + message.subject();
		_recipientList.clear();
		_recipientList.add(new Member(message.senderMemberId(), message.senderName()));
		_replyToMessage = message;
	}
	private boolean isReply() { return _replyToMessage != null; }
	
	public static void setupSendMessageTo(Member member) {
		_subjectIn = "";
		_replyToMessage = null;
		_recipientList = new ArrayList<Member>();
		_recipientList.add(member);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - send a message"; }
	
	////////////////////////////////////////////////////////////////////////
	//// UI Members
	
	private EditText _recipient;
	private EditText _subject;
	private EditText _body;
	
	private EditText _event;
	
	private CheckBox _withConfirmation;
	private CheckBox _withAlert;
	
	private Button _send;
	
	/////////////////////////////////////////////////////////////////////
	//// Data Members
		
		
	////////////////////////////////////////////////////////////////////////
	/// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		bindView();
	}
	
	
	private void initializeView() {
		setContentView(R.layout.message_send);
		
		_recipient = (EditText) findViewById(R.id.txtRecipients);
		_subject = (EditText) findViewById(R.id.txtSubject);
		_body = (EditText) findViewById(R.id.txtBody);
		
		_event = (EditText) findViewById(R.id.txtEvent);
		
		_withConfirmation = (CheckBox) findViewById(R.id.checkConfirm);
		_withAlert = (CheckBox) findViewById(R.id.checkAlert);
		
		_send = (Button) findViewById(R.id.btnSend);
		
		_recipient.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setRecipients(); }
		});
				
		_event.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setEvent(); }
		});
		
		_send.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { sendMessage(); }
		});
	}
	
	private void bindView() {
		// Bind others.		
		bindRecipients();
		bindEvent();
		_subject.setText(_subjectIn);
	}
	
	private void bindRecipients() {
		_recipient.setText(ArrayListUtils.toString(_recipientList, "; ", new GetString<Member> () { 
			@Override public String getString(Member obj) { return obj.memberName(); }
		}));
	}
	
	private void bindEvent() {
		_event.setText(hasEvent() 
							? getSelectedEvent().toPrettyString()
							: "");		
	}
	
	@Override
	protected void updateRecipientList(ArrayList<Member> recipients) {
		_recipientList = recipients;
		bindRecipients();
	}
	
	@Override
	protected void updateEvent() {
		bindEvent();
	}
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Sends a new message."));
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	/// Event Handlers
	
	private void setRecipients() {
		if (!isReply()) {
			setNewRecipients(_recipientList);
		}
	}
	
	private void setEvent() {
		loadUpcomingEvents();
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	//// Creating Message
	
	@Override
	protected void cleanupExtra() {
		_subjectIn = "";
		_recipientList.clear();
		_replyToMessage = null;
	}

	@Override
	protected NewMessageInfo getMessage() {
		NewMessageInfo message = new NewMessageInfo();
		
		ArrayList<String> recipientIds = new ArrayList<String>();
		for(Member recipient : _recipientList) recipientIds.add(recipient.memberId()); 
		
		message.recipients(recipientIds);
		message.subject(_subject.getText().toString());
		message.body(_body.getText().toString());
		message.eventId(hasEvent() ? getSelectedEvent().eventId() : null);
		message.eventType(hasEvent() ? getSelectedEvent().eventType() : null);
		message.isAlert(_withAlert.isChecked());
		message.isPublic(_withConfirmation.isChecked());
		message.type(Message.Type.Plain);
		message.includeFans(false);	// TODO?
		
		return message;
	}
}
