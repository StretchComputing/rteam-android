package com.rteam.android.messaging.common;

import java.util.ArrayList;

import com.rteam.android.R;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MessagesForListAdapter extends BaseExpandableListAdapter {
	
	public interface MessageCheckedHandler {
		public void onMessageChecked(MessageInfo message, boolean checked);
	}
	
	private LayoutInflater _inflater;
	
	private ArrayList<MessageInfo> _inbox;
	private ArrayList<MessageInfo> _outbox;
	
	private MessageCheckedHandler _messageCheckedHandler;
	
	public MessagesForListAdapter(Context context, ArrayList<MessageInfo> inbox, ArrayList<MessageInfo> outbox, MessageCheckedHandler messageCheckedHandler) {
		_inflater = LayoutInflater.from(context);
		_inbox = inbox;
		_outbox = outbox;
		_messageCheckedHandler = messageCheckedHandler;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) { return (groupPosition == 0 ? _inbox : _outbox).get(childPosition); }
	@Override
	public long getChildId(int groupPosition, int childPosition) { return groupPosition == 0 ? childPosition : -childPosition; }
	@Override
	public int getChildrenCount(int groupPosition) { return (groupPosition == 0 ? _inbox : _outbox).size(); }
	@Override
	public Object getGroup(int groupPosition) { return groupPosition == 0 ? _inbox : _outbox; }
	@Override
	public int getGroupCount() { return 2; }
	@Override
	public long getGroupId(int groupPosition) { return groupPosition; }
	@Override
	public boolean hasStableIds() { return false; }
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) { return false; 	}	

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String title = groupPosition == 0 ? "Inbox" : "Outbox";
		convertView = _inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
		((TextView) convertView.findViewById(android.R.id.text1)).setText(title);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		MessageInfo message = (MessageInfo) getChild(groupPosition, childPosition);
		convertView = _inflater.inflate(R.layout.message_all_for_item, null);
		bindView(message, convertView, groupPosition == 1);
		return convertView;
	}

	private final int SUBJECT_LENGTH = 20;
	private final int BODY_LENGTH = 40;
	
	private void bindView(final MessageInfo message, View view, boolean isOutbox) {
		CheckBox checkSelected = (CheckBox) view.findViewById(R.id.checkSelected);
		TextView txtSubject = (TextView) view.findViewById(R.id.txtSubject);
		TextView txtBody = (TextView) view.findViewById(R.id.txtBody);
		TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
		TextView txtPollResults = (TextView) view.findViewById(R.id.txtPollResults);
		TextView txtType = (TextView) view.findViewById(R.id.txtType);
		
		
		txtSubject.setText(StringUtils.truncate(message.subject(), SUBJECT_LENGTH));
		txtBody.setText(StringUtils.truncate(message.body(), BODY_LENGTH));
		txtDate.setText(DateUtils.toPrettyString(message.displayDate()));
		txtType.setText(message.type().toStringPretty());
		
		if (message.type() == Message.Type.Poll && isOutbox) {
			txtPollResults.setText(new StringBuilder().append(message.getNumberResponses()).append("/").append(message.getNumberResponders()).append(" responses recieved.").toString());
		}
		else {
			txtPollResults.setVisibility(View.GONE);
		}
		
		checkSelected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { _messageCheckedHandler.onMessageChecked(message, isChecked); }
		});
	}
	

}
