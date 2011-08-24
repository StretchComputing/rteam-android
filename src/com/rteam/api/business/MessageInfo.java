package com.rteam.api.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.rteam.api.common.DateUtils;
import com.rteam.api.common.EnumUtils;
import com.rteam.api.common.JSONUtils;
import com.rteam.api.common.StringUtils;

public class MessageInfo implements Serializable, Comparable<MessageInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class MemberResponseInfo {
		private Boolean _belongsToUser;
		public Boolean belongsToUser() { return _belongsToUser; }
		public void belongsToUser(Boolean value) { _belongsToUser = value; }
		
		private String _memberId;
		public String memberId() { return _memberId; }
		public void memberId(String value) { _memberId = value; }
		
		private String _memberName;
		public String memberName() { return _memberName; }
		public void memberName(String value) { _memberName = value; }
		
		private String _reply;
		public String reply() { return _reply; }
		public void reply(String value) { _reply = value; }
		
		private String _replyEmailAddress;
		public String replyEmailAddress() { return _replyEmailAddress; }
		public void replyEmailAddress(String value) { _replyEmailAddress = value; }
		
		private Date _replyDate;
		public Date replyDate() { return _replyDate; }
		public void replyDate(Date value) { _replyDate = value; }
		
		public MemberResponseInfo(JSONObject json) {
			belongsToUser(json.optBoolean("belongsToUser"));
			memberId(json.optString("memberId"));
			memberName(json.optString("memberName"));
			reply(json.optString("reply"));
			replyEmailAddress(json.optString("replyEmailAddress"));
			replyDate(JSONUtils.parseDate(json.optString("replyDate")));
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/// Class Members
	
	private String _teamId;
	public String teamId() { return _teamId; }
	public void teamId(String value) { _teamId = value; }
	
	private String _teamName;
	public String teamName() { return _teamName; }
	public void teamName(String value) { _teamName = value; }
	
	private String _messageThreadId;
	public String messageThreadId() { return _messageThreadId; }
	public void messageThreadId(String value) { _messageThreadId = value; }
	
	private String _subject;
	public String subject() { return _subject; }
	public void subject(String value) { _subject = value; }
	
	private String _body;
	public String body() { return _body; }
	public void body(String value) { _body = value; }
	
	private ArrayList<String> _pollChoices;
	public ArrayList<String> pollChoices() { return _pollChoices; }
	public void pollChoices(ArrayList<String> value) { _pollChoices = value; }
	
	private Message.Type _type;
	public Message.Type type() { return _type; }
	public void type(Message.Type value) { _type = value; }
	
	private String _eventId;
	public String eventId() { return _eventId; }
	public void eventId(String value) { _eventId = value; }
	
	private Event.Type _eventType;
	public Event.Type eventType() { return _eventType; }
	public void eventType(Event.Type value) { _eventType = value; }
	
	private Message.Status _status;
	public Message.Status status() { return _status; }
	public void status(Message.Status value) { _status = value; }
	
	private Date _receivedDate;
	public Date receivedDate() { return _receivedDate; }
	public void receivedDate(Date value) { _receivedDate = value; }
	
	private Date _createdDate;
	public Date createdDate() { return _createdDate; }
	public void createdDate(Date value) { _createdDate = value; }
	
	private Date _finalizedDate;
	public Date finalizedDate() { return _finalizedDate; }
	public void finalizedDate(Date value) { _finalizedDate = value; }
	
	private Boolean _isReminder;
	public Boolean isReminder() { return _isReminder; }
	public void isReminder(Boolean value) { _isReminder = value; }
	
	private Boolean _wasViewed;
	public Boolean wasViewed() { return _wasViewed; }
	public void wasViewed(Boolean value) { _wasViewed = value; }
	
	private String _followUpMessage;
	public String followUpMessage() { return _followUpMessage; }
	public void followUpMessage(String value) { _followUpMessage = value; }
	
	private String _senderMemberId;
	public String senderMemberId() { return _senderMemberId; }
	public void senderMemberId(String value) { _senderMemberId = value; }
	
	private String _senderName;
	public String senderName() { return _senderName; }
	public void senderName(String value) { _senderName = value;  }
	
	private Collection<MemberResponseInfo> _members;
	public Collection<MemberResponseInfo> members() { return _members; }
	public void members(Collection<MemberResponseInfo> value) { _members = value; }
	
	private Boolean _isPublic;
	public Boolean isPublic() { return _isPublic; }
	public void isPublic(Boolean value) { _isPublic = value; }
	
	public Date displayDate() {
		if (receivedDate() != null) return receivedDate();
		if (createdDate() != null) return createdDate();
		if (finalizedDate() != null) return finalizedDate();
		return new Date();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// .ctor
	
	public MessageInfo() {}
	
	public MessageInfo(JSONObject json) {
		teamId(json.optString("teamId"));
		teamName(json.optString("teamName"));
		messageThreadId(json.optString("messageThreadId"));
		subject(json.optString("subject"));
		body(json.optString("body"));
		eventId(json.optString("eventId"));
		eventType(EnumUtils.fromString(Event.Type.class, json.optString("eventType"), Event.Type.None));
		status(EnumUtils.fromString(Message.Status.class, json.optString("status"), Message.Status.None));
		pollChoices(JSONUtils.convertToStrings(json.optJSONArray("pollChoices")));
		members(convertToMemberList(json.optJSONArray("members")));
		receivedDate(DateUtils.parse(json.optString("receivedDate")));
		createdDate(DateUtils.parse(json.optString("createdDate")));
		finalizedDate(DateUtils.parse(json.optString("finalizedDate")));
		isReminder(json.optBoolean("isReminder"));
		wasViewed(json.optBoolean("wasViewed"));
		followUpMessage(json.optString("followUpMessage"));
		senderMemberId(json.optString("senderMemberId"));
		senderName(json.optString("senderName"));
		isPublic(json.optBoolean("isPublic"));
		
		type(pollChoices().size() > 0 ? Message.Type.Poll : Message.Type.Message);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Helpers
	
	
	/// Poll Response Helpers
	
	public int getNumberResponses() {
		int numResponded = 0;
		if (members() != null) {
			for(MemberResponseInfo member : members()) {
				if (!StringUtils.isNullOrEmpty(member.reply()))
					numResponded++;
			}
		}
		return numResponded;
	}
	
	public int getNumberResponders() { return members() != null ? members().size() : 0; }
	
	public boolean isPollCompleted() { return status() == Message.Status.Finalized; }
	
	public HashMap<String, Integer> getPollResponses() {
		HashMap<String, Integer> responses = new HashMap<String, Integer>();
		
		for (String pollChoice : pollChoices()) {
			responses.put(pollChoice, 0);
		}
		
		if (members() != null) {
			for (MemberResponseInfo member : members()) {
				if (!StringUtils.isNullOrEmpty(member.reply()) && responses.containsKey(member.reply())) {
					responses.put(member.reply(), responses.get(member.reply()) + 1);
				}
			}
		}
			
		return responses;
	}
	
	public MemberResponseInfo getUserResponse() {
		if (members() != null) {
			for (MemberResponseInfo member : members()) {
				if (member.belongsToUser()) { return member; }
			}
		}
		return null;
	}
	
	public boolean hasReplied() {
		MemberResponseInfo member = getUserResponse();
		return member != null && !StringUtils.isNullOrEmpty(member.reply());
	}
	
	public String getReply() {
		return getUserResponse().reply();
	}
	
	public void setReply(String reply) {
		MemberResponseInfo member = getUserResponse();
		if (member != null) {
			member.reply(reply);
		}
	}

	/// General Helpers
	
	public boolean hasFollowup() { return !StringUtils.isNullOrEmpty(followUpMessage()); }
	
	public void merge(MessageInfo other) {
		members(other.members());
	}
	
	/// Parsing Members Helpers 
	
	
	private ArrayList<MemberResponseInfo> convertToMemberList(JSONArray array) {
		if (array == null) return null;
		
		ArrayList<MemberResponseInfo> members = new ArrayList<MemberResponseInfo>();
		for(int i=0; i<array.length(); i++) {
			JSONObject m = array.optJSONObject(i);
			if (m != null) members.add(new MemberResponseInfo(m));
		}
		return members;
	}
	
	
	
	/// Equality Overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MessageInfo))
			return false;
		MessageInfo other = (MessageInfo)o;
		
		return (messageThreadId() != null && other.messageThreadId() != null && messageThreadId() == other.messageThreadId());
	}
	
	@Override
	public int hashCode() {
		if (messageThreadId() == null)
			return super.hashCode();
		return messageThreadId().hashCode();
	}
	
	@Override
	public String toString() {
		return messageThreadId() != null
				? messageThreadId()
				: "";
	}
	
	@Override
	public int compareTo(MessageInfo another) {
		return receivedDate().compareTo(another.receivedDate());
	}
}
