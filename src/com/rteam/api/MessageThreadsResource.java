package com.rteam.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.RTeamApplicationVersion;
import com.rteam.api.common.TimeZoneUtils;
import com.rteam.api.common.UriBuilder;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.Message;
import com.rteam.api.business.MessageFilters;
import com.rteam.api.business.MessageInfo;
import com.rteam.api.business.NewMessageInfo;
import com.rteam.api.business.UpdateMessageInfo;

public class MessageThreadsResource extends ResourceBase {

	/////////////////////////////////////////////////////////////////////////////////////////
	/// .ctor
	
	public static MessageThreadsResource instance() {
		if (_instance == null) _instance = new MessageThreadsResource();
		return _instance;
	}
	
	private static MessageThreadsResource _instance;
	
	private MessageThreadsResource() {
		super(AndroidTokenStorage.get(), RTeamApplicationVersion.get());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Classes
	
	public class GetMessageCountResponse extends ResourceResponse {

		private int _messageCount;
		public int messageCount() { return _messageCount; }
		
		private boolean _newActivity;
		public boolean newActivity() { return _newActivity; }
		
		protected GetMessageCountResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_messageCount = json().optInt("count");
				_newActivity = json().optBoolean("newActivity");
			}
		}
	}
	
	public interface GetMessageCountResponseHandler {
		public void finish(GetMessageCountResponse response);
	}
	
	public class GetMessagesResponse extends ResourceResponse {
		
		private ArrayList<MessageInfo> _inboxMessages;
		public ArrayList<MessageInfo> getInboxMessages() { return _inboxMessages; }
		
		private ArrayList<MessageInfo> _outboxMessages;
		public ArrayList<MessageInfo> getOutboxMessages() { return _outboxMessages; }
		

		protected GetMessagesResponse(APIResponse response, MessageFilters filters) {
			super(response);			
			initialize(response, filters);
		}
		
		private void initialize(APIResponse response, MessageFilters filters) {
			_inboxMessages = new ArrayList<MessageInfo>();
			_outboxMessages = new ArrayList<MessageInfo>();
			
			if (isResponseGood()) {
				_inboxMessages = getMessages(response.getJSONResponse().optJSONArray("inbox"), filters);
				_outboxMessages = getMessages(response.getJSONResponse().optJSONArray("outbox"), filters);
			}
		}
		
		private ArrayList<MessageInfo> getMessages(JSONArray array, MessageFilters filters) {
			ArrayList<MessageInfo> messages = new ArrayList<MessageInfo>();
			if (array != null) {
				for(int i=0; i<array.length(); i++) {
					JSONObject m = array.optJSONObject(i);
					if (m != null) {
						messages.add(new MessageInfo(m, filters.teamId(), filters.eventId()));
					}
				}
			}
			
			return messages;
		}
		
	}
		
	public interface GetMessagesResponseHandler {
		public void getMessageThreadsFinish(GetMessagesResponse response);
	}
	
	public class CreateMessageResponse extends ResourceResponse {

		private String _messageThreadId;
		public String getMessageThreadId() { return _messageThreadId; } 
		
		protected CreateMessageResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		
		private void initialize() {
			if (isResponseGood()) {
				_messageThreadId = json().optString("messageThreadId");
			}
		}
	}
	
	public interface CreateMessageResponseHandler {
		public void finish(CreateMessageResponse response);
	}
	
	public class ArchiveMessagesResponse extends ResourceResponse {

		protected ArchiveMessagesResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
		
	}
	
	public interface ArchiveMessagesResponseHandler {
		public void finish(ArchiveMessagesResponse response);
	}
	
	public class UpdateMessageResponse extends ResourceResponse {
		protected UpdateMessageResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface UpdateMessageResponseHandler {
		public void finish(UpdateMessageResponse response);
	}
	
	public class GetMessageResponse extends ResourceResponse {

		private MessageInfo _message;
		public MessageInfo getMessage() { return _message; }
		
		protected GetMessageResponse(APIResponse response) {
			super(response);
			initialize(response);
		}
		
		private void initialize(APIResponse response) {
			if (isResponseGood()) {
				_message = new MessageInfo(json());
			}
		}
	}
	
	public interface GetMessageResponseHandler {
		public void finish(GetMessageResponse response);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	// Exposed Methods
	
	public GetMessageCountResponse getMessageCount() {
		return new GetMessageCountResponse(get(createBuilder().addPath("messageThreads").addPath("count").addPath("new")));
	}
	
	public void getMessageCount(final GetMessageCountResponseHandler handler) {
		(new AsyncTask<Void, Void, GetMessageCountResponse>() {
			@Override
			protected GetMessageCountResponse doInBackground(Void... params) {
				return getMessageCount();
			}
			
			@Override
			protected void onPostExecute(GetMessageCountResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	public GetMessagesResponse getMessageThreads(MessageFilters filters) {
		UriBuilder uri = createBuilder()
							.addPathIf("team", filters.hasTeamId())
							.addPathIf(filters.teamId(), filters.hasTeamId())
							.addPath("messageThreads")
							.addPath(filters.timeZone())
							.addParams(filters.getParams());
		return new GetMessagesResponse(get(uri), filters);
	}
	
	public void getMessageThreads(final MessageFilters filters, final GetMessagesResponseHandler handler) {
		(new AsyncTask<Void, Void, GetMessagesResponse>() {
			@Override
			protected GetMessagesResponse doInBackground(Void... params) {
				return getMessageThreads(filters);
			}
			
			@Override
			protected void onPostExecute(GetMessagesResponse result) {
				handler.getMessageThreadsFinish(result);
			}
		}).execute();
	}
		
	public CreateMessageResponse createMessage(String teamId, NewMessageInfo message) {
		return new CreateMessageResponse(post(createBuilder().addPath("team").addPath(teamId).addPath("messageThreads"), message.toJSON()));
	}
	
	public void createMessage(final String teamId, final NewMessageInfo message, final CreateMessageResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateMessageResponse>(){
			@Override
			protected CreateMessageResponse doInBackground(Void... params) {
				return createMessage(teamId, message);
			}
			
			@Override
			protected void onPostExecute(CreateMessageResponse result) {
				handler.finish(result);
			}
		}).execute();
	}
	
	public ArchiveMessagesResponse archive(ArrayList<MessageInfo> messages, Message.Group location) {
		JSONObject json = new JSONObject();
		
		try {
			json.put("messageLocation", location.toString());
			json.put("status", "archived");
			
			JSONArray ids = new JSONArray();
			for(MessageInfo msg : messages) {
				ids.put(msg.messageThreadId());
			}
			
			json.put("messageThreadIds", ids);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArchiveMessagesResponse(put(createBuilder().addPath("messageThreads"), json));
	}
	
	public void archive(final ArrayList<MessageInfo> messages, final Message.Group location, final ArchiveMessagesResponseHandler handler) {
		(new AsyncTask<Void, Void, ArchiveMessagesResponse>(){
			@Override
			protected ArchiveMessagesResponse doInBackground(Void... params) {
				return archive(messages, location);
			}
			
			@Override
			protected void onPostExecute(ArchiveMessagesResponse result) {
				if (handler != null) {
					handler.finish(result);
				}
			}
		}).execute();
	}

	////////////////////////////////////////////////////////////////////////////////////
	// Direct Methods (Synchronous)

	public UpdateMessageResponse updateMessageThread(UpdateMessageInfo update) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(update.teamId())
							.addPath("messageThread").addPath(update.messageId());
		return new UpdateMessageResponse(put(uri, update.toJSON()));
	}
	
	public GetMessageResponse getMessageThread(String teamId, String messageThreadId, boolean includeMemberInfo) {
		return getMessageThread(TimeZoneUtils.getTimeZone(), teamId, messageThreadId, includeMemberInfo);
	}
	
	public GetMessageResponse getMessageThread(String timeZone, String teamId, String messageThreadId, boolean includeMemberInfo) {
		UriBuilder uri = createBuilder()
							.addPath("team").addPath(teamId)
							.addPath("messageThread").addPath(messageThreadId)
							.addPath(timeZone)
							.addParam("includeMemberInfo", Boolean.toString(includeMemberInfo));
		return new GetMessageResponse(get(uri));
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// Asynchronous Methods

	public void updateMessageThread(final UpdateMessageInfo update, final UpdateMessageResponseHandler handler) {
		(new AsyncTask<Void, Void, UpdateMessageResponse>(){

			@Override
			protected UpdateMessageResponse doInBackground(Void... params) {
				return updateMessageThread(update);
			}
			
			@Override
			protected void onPostExecute(UpdateMessageResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	public void getMessageThread(final String teamId, final String messageThreadId, final boolean includeMemberInfo, final GetMessageResponseHandler handler) {
		(new AsyncTask<Void, Void, GetMessageResponse>(){

			@Override
			protected GetMessageResponse doInBackground(Void... params) {
				return getMessageThread(teamId, messageThreadId, includeMemberInfo);
			}
			
			@Override
			protected void onPostExecute(GetMessageResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	public void getMessageThread(final String timeZone, final String teamId, final String messageThreadId, final boolean includeMemberInfo, final GetMessageResponseHandler handler) {
		(new AsyncTask<Void, Void, GetMessageResponse>(){

			@Override
			protected GetMessageResponse doInBackground(Void... params) {
				return getMessageThread(timeZone, teamId, messageThreadId, includeMemberInfo);
			}
			
			@Override
			protected void onPostExecute(GetMessageResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
}
