package com.rteam.android.messaging;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rteam.android.R;
import com.rteam.android.messaging.common.CreateMessageBase;
import com.rteam.api.business.Member;
import com.rteam.api.business.Message;
import com.rteam.api.business.NewMessageInfo;
import com.rteam.api.business.Poll;
import com.rteam.api.common.ArrayListUtils;
import com.rteam.api.common.StringUtils;
import com.rteam.api.common.ArrayListUtils.GetString;

public class CreatePoll extends CreateMessageBase {
	
	////////////////////////////////////////////////////////////////////////////////////////
	/// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - create poll"; }
	
	
	private EditText _recipient;
	private EditText _subject;
	private EditText _pollQuestion;
	
	private EditText _event;
	
	private EditText _pollChoices;
	
	private CheckBox _showResults;
	private Button _send;
	
		
	private Poll.Type _pollType;
	
	private ArrayList<Member> _recipientList = new ArrayList<Member>();
	private ArrayList<String> _pollChoiceList = new ArrayList<String>();
	
	///////////////////////////////////////////////////////////////////////////////////////
	/// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		bindView();
	}
	
	private void initializeView() {
		setContentView(R.layout.message_poll_create);
		
		_recipient = (EditText) findViewById(R.id.txtRecipients);
		_subject = (EditText) findViewById(R.id.txtSubject);
		_pollQuestion = (EditText) findViewById(R.id.txtBody);
		
		_event = (EditText) findViewById(R.id.txtEvent);
		_pollChoices = (EditText) findViewById(R.id.txtPollChoices);
		_showResults = (CheckBox) findViewById(R.id.checkShowResults);
		
		_send = (Button) findViewById(R.id.btnSend);
		
		_recipient.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setNewRecipients(_recipientList); }
		});
		
		_pollChoices.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setPollChoices(); }
		});
		
		_send.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { sendMessage(); }
		});
		
		_event.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setEvent(); }
		});
	}
	
		
	private void bindView() {
		bindRecipientList();
		bindEvent();
		bindPollChoices();
	}
	
	private void bindRecipientList() {
		_recipient.setText(ArrayListUtils.toString(_recipientList, "; ", new GetString<Member> () { 
			@Override public String getString(Member obj) { return obj.memberName(); }
		}));
	}
	
	private void bindEvent() {
		_event.setText(hasEvent()
						? getSelectedEvent().toPrettyString()
						: "");		
	}
	
	private void bindPollChoices() {
		_pollChoices.setText(ArrayListUtils.toString(_pollChoiceList, "; "));
	}
	
	@Override
	protected void updateRecipientList(ArrayList<Member> recipients) {
		_recipientList = recipients;
		bindRecipientList();
	}	
	
	@Override
	protected void updateEvent() {
		bindEvent();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/// Event Handlers
	
	private void setPollChoices() {
		showPollChoicesDialog();
	}
	
	private void setEvent() {
		loadUpcomingEvents();
	}
		
	//////////////////////////////////////////////////////////////////////////////////
	//// Creating Message
	
	@Override
	protected void cleanupExtra() {
		_recipientList.clear();
		_pollChoiceList.clear();
	}
	
	@Override
	protected NewMessageInfo getMessage() {
		NewMessageInfo message = new NewMessageInfo();
		
		ArrayList<String> recipientIds = new ArrayList<String>();
		for(Member recipient : _recipientList) recipientIds.add(recipient.memberId()); 
		
		message.recipients(recipientIds);
		message.subject(_subject.getText().toString());
		message.body(_pollQuestion.getText().toString());
		message.eventId(hasEvent() ? getSelectedEvent().eventId() : null);
		message.eventType(hasEvent() ? getSelectedEvent().eventType() : null);
		message.isPublic(_showResults.isChecked());
		message.isAlert(false); // TODO ?
		message.type(_pollType == Poll.Type.YesNo ? Message.Type.Confirm : Message.Type.Poll);
		message.includeFans(false);	// TODO?
		message.pollChoices(_pollChoiceList);
		
		return message;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	/// Selecting Poll Choices
	
	private void showPollChoicesDialog() {
		final ArrayList<String> pollTypes = new ArrayList<String>();
		for(Poll.Type type : Poll.Type.values()) {
			pollTypes.add(type.toStringPretty());
		}
				
		new AlertDialog.Builder(this)
			 .setTitle("Select Poll Style")
			 .setItems(pollTypes.toArray(new String[2]), 
					   new DialogInterface.OnClickListener() {	
						@Override
						public void onClick(DialogInterface dialog, int which) { selectPollStyle(Poll.Type.valueOfPretty(pollTypes.get(which))); } 
					  })
			  .show();
	}
	
	private void selectPollStyle(Poll.Type pollType) {
		_pollType = pollType;
		
		if (pollType == Poll.Type.YesNo) {
			_pollChoiceList.clear();
			_pollChoiceList.add("Yes");
			_pollChoiceList.add("No");
			
			bindPollChoices();
		}
		else if (pollType == Poll.Type.Custom) {
			showCustomPollChoicesDialog();	
		}
	}
	
	private void showCustomPollChoicesDialog() {	
		final View customView = getCustomPollChoiceView();
		
		new AlertDialog.Builder(this)
			 .setView(customView)
			 .setPositiveButton("Done", 
					 			new DialogInterface.OnClickListener() {
				 					@Override
		 							public void onClick(DialogInterface d, int which) { 
				 						showCustomPollChoicesDialogDone(customView); 
			 						}
			 					})
			.setNegativeButton("Cancel",
							   new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {}
								})
			.show();
	}
	
	private View getCustomPollChoiceView() {
		View customView = View.inflate(this, R.layout.dlg_pollchoices, null);
		PollChoiceViewHolder holder = new PollChoiceViewHolder();
		holder.txtPollChoices.add((EditText) customView.findViewById(R.id.txtPollChoice1));
		holder.txtPollChoices.add((EditText) customView.findViewById(R.id.txtPollChoice2));
		holder.txtPollChoices.add((EditText) customView.findViewById(R.id.txtPollChoice3));
		holder.txtPollChoices.add((EditText) customView.findViewById(R.id.txtPollChoice4));
		holder.txtPollChoices.add((EditText) customView.findViewById(R.id.txtPollChoice5));
		
		customView.setTag(holder);
		
		for (int i=0; i<_pollChoiceList.size(); i++) {
			holder.txtPollChoices.get(i).setText(_pollChoiceList.get(i));
		}
		
		return customView;
	}
	
	private void showCustomPollChoicesDialogDone(View dialogView) {
		PollChoiceViewHolder holder = (PollChoiceViewHolder) dialogView.getTag();
		
		_pollChoiceList.clear();
		
		for (EditText txt : holder.txtPollChoices) {
			if (!StringUtils.isNullOrEmpty(txt.getText().toString())) {
				_pollChoiceList.add(txt.getText().toString());
			}
		}
				
		bindPollChoices();
	}	
	
	
	private static class PollChoiceViewHolder {
		public ArrayList<EditText> txtPollChoices = new ArrayList<EditText>();
	}
}

