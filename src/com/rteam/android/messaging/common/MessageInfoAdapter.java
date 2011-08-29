package com.rteam.android.messaging.common;

import java.util.ArrayList;
import com.rteam.android.R;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageInfoAdapter extends BaseAdapter {

	private LayoutInflater _inflater;
	private ArrayList<MessageInfo> _messages;
	
	public MessageInfoAdapter(Context context, ArrayList<MessageInfo> messages) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		_inflater = LayoutInflater.from(context);
        _messages = messages;
	}
	
	
	
	@Override
	public int getCount() {
		return _messages.size();
	}

	@Override
	public Object getItem(int position) {
		return _messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageInfo message = (MessageInfo) getItem(position);
		
		MessageViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = _inflater.inflate(R.layout.list_message_info, null);
				
			viewHolder = new MessageViewHolder();
			viewHolder.messageSubject = ((TextView) convertView.findViewById(R.id.textSubject));
			viewHolder.messageSummary = ((TextView) convertView.findViewById(R.id.textMessageSummary));
			viewHolder.messageTimeStamp = ((TextView) convertView.findViewById(R.id.textMessageTimestamp));
			viewHolder.teamSummary = ((TextView) convertView.findViewById(R.id.textTeamSummary));
			viewHolder.resultsCompleted = ((TextView) convertView.findViewById(R.id.textPollCompleted));
			
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (MessageViewHolder) convertView.getTag();
		}
		
		setupView(convertView, viewHolder, message);
		
		return convertView;
	}
	
	private final int SUBJECT_LENGTH = 20;
	private final int BODY_LENGTH = 40;
	
	private void setupView(View view, MessageViewHolder viewHolder, MessageInfo message) {
		boolean isPoll = message.type() == Message.Type.Poll;
		boolean wasViewed = message.wasViewed();
		
		viewHolder.messageSubject.setText(StringUtils.truncate(message.subject(), SUBJECT_LENGTH));
		viewHolder.messageSummary.setText(StringUtils.truncate(message.body(), BODY_LENGTH));
		viewHolder.messageTimeStamp.setText(DateUtils.toShortString(message.receivedDate()));
		viewHolder.teamSummary.setText("Team: " + message.teamName());
		
		if (isPoll) {
			viewHolder.resultsCompleted.setText(message.isPollCompleted() ? "*complete*" : "");
			viewHolder.resultsCompleted.setVisibility(View.VISIBLE);
		} else {
			viewHolder.resultsCompleted.setVisibility(View.INVISIBLE);
		}
		
		// Adjust for read/unread
		view.setBackgroundResource(wasViewed ? R.color.message_read : R.color.message_unread);
		viewHolder.messageSubject.setTypeface(viewHolder.messageSubject.getTypeface(), wasViewed ? Typeface.NORMAL : Typeface.BOLD);
		viewHolder.messageSummary.setTypeface(viewHolder.messageSummary.getTypeface(), wasViewed ? Typeface.NORMAL : Typeface.BOLD);
		viewHolder.messageTimeStamp.setTypeface(viewHolder.messageTimeStamp.getTypeface(), wasViewed ? Typeface.NORMAL : Typeface.BOLD);
		viewHolder.teamSummary.setTypeface(viewHolder.teamSummary.getTypeface(), wasViewed ? Typeface.NORMAL : Typeface.BOLD);
		viewHolder.resultsCompleted.setTypeface(viewHolder.resultsCompleted.getTypeface(), wasViewed ? Typeface.NORMAL : Typeface.BOLD);
	}
	
	
	static class MessageViewHolder {
		TextView messageSubject;
		TextView messageSummary;
		TextView messageTimeStamp;
		TextView teamSummary;
		TextView resultsCompleted;
	}
}
