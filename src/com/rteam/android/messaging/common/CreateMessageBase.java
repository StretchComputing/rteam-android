package com.rteam.android.messaging.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.Simple3LineAdapater;
import com.rteam.android.events.common.EventLoader;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.MembersResource;
import com.rteam.api.MessageThreadsResource;
import com.rteam.api.MembersResource.MemberListResponse;
import com.rteam.api.MembersResource.MemberListResponseHandler;
import com.rteam.api.MessageThreadsResource.CreateMessageResponse;
import com.rteam.api.base.ResponseStatus;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Member;
import com.rteam.api.business.NewMessageInfo;
import com.rteam.api.business.Team;
import com.rteam.api.common.DateUtils;

public abstract class CreateMessageBase extends RTeamActivityChildTab {

	//////////////////////////////////////////////////////////////
	/// Static Members
	private static Team _selectedTeam;
	public static void setupTeam(Team team) { _selectedTeam = team; }
	
	private static EventBase _selectedEvent;
	public static void setupEvent(EventBase event) { _selectedEvent = event; }
	
	private static boolean _clearOnSend;
	public static void clearOnSend(boolean clear) { _clearOnSend = clear; }
	
	@Override
	protected void destroy() {
		_selectedTeam = null;
		_selectedEvent = null;
		_clearOnSend = false;
	}
	
	
	//////////////////////////////////////////////////////////////
	/// Members
	private List<Team> _teams;
	private ArrayList<Member> _members;
		
	private ArrayList<Member> _selectedRecipientList;
	private ArrayList<Member> _pendingRecipientList;
	
	
	private ArrayList<EventBase> _allEvents;
	private ArrayList<EventBase> _upcomingEvents;
	
	
	//////////////////////////////////////////////////////////////
	/// Accessors
	
	protected boolean hasTeam() { return _selectedTeam != null; }
	protected Team getSelectedTeam() { return _selectedTeam; }
	
	protected boolean hasEvent() { return _selectedEvent != null; }
	protected EventBase getSelectedEvent() { return _selectedEvent; } 
	
	protected abstract void updateRecipientList(ArrayList<Member> recipients);
	protected abstract void updateEvent();
	
	//////////////////////////////////////////////////////////////
	/// Setting Recipients
	protected void setNewRecipients(ArrayList<Member> recipientList) {
		_selectedRecipientList = recipientList;
		
		if (!hasTeam()) {
			CustomTitle.setLoading(true, "Loading Teams...");
			_teams = TeamCache.getTeams();
			showTeamsDialog();
		}
		else {
			loadTeamMembers();
		}
	}	
	
	private void showTeamsDialog() {
		CustomTitle.setLoading(false);
		if (isFinishing()) return;
		
		ArrayList<String> teamNames = new ArrayList<String>();		
		for(Team team : _teams) {
			teamNames.add(team.teamName());
		}
		
		new AlertDialog.Builder(this)
			.setTitle("Select a Team")
			.setItems(teamNames.toArray(new String[teamNames.size()]), 
					  new DialogInterface.OnClickListener() {	
						@Override
						public void onClick(DialogInterface dialog, int which) { selectTeam(which); } 
					  })
			.show();
	}
	
	private void selectTeam(int index) {
		if (index >= 0 && index < _teams.size()) {
			_selectedTeam = _teams.get(index);
			loadTeamMembers();
		}
	}
	
	private void loadTeamMembers() {
		CustomTitle.setLoading(true, "Loading Members...");
		MembersResource.instance().getMembers(_selectedTeam.teamId(), true, new MemberListResponseHandler() {
			@Override
			public void finish(MemberListResponse response) { loadTeamMembersFinished(response); }
		});	
	}
	
	private void loadTeamMembersFinished(MemberListResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_members = response.members();
			showMembersDialog();
		}
	}
	
	
	private boolean allMembersFans(ArrayList<Member> members) {
		ArrayList<Member.Role> roles = new ArrayList<Member.Role>();
		roles.add(Member.Role.Fan);
		return allMembersType(members, roles);
	}
	private boolean allMembersTeam(ArrayList<Member> members) {
		ArrayList<Member.Role> roles = new ArrayList<Member.Role>();
		roles.add(Member.Role.Coordinator);
		roles.add(Member.Role.Member);
		return allMembersType(members, roles);
	}
	private boolean allMembersType(ArrayList<Member> members, ArrayList<Member.Role> types) {
		if (members.size() == 0) return false;
		for(Member member : members) {
			if (!types.contains(member.participantRole())) return false;
		}
		return true;
	}
	
	private ArrayList<Member> getMembersTeam(ArrayList<Member> members) {
		ArrayList<Member.Role> roles = new ArrayList<Member.Role>();
		roles.add(Member.Role.Fan);
		return getMembersType(members, roles);
	}
	
	private ArrayList<Member> getMembersFans(ArrayList<Member> members) {
		ArrayList<Member.Role> roles = new ArrayList<Member.Role>();
		roles.add(Member.Role.Coordinator);
		roles.add(Member.Role.Member);
		return getMembersType(members, roles);
	}
	
	private ArrayList<Member> getMembersType(ArrayList<Member> members, ArrayList<Member.Role> types) {
		ArrayList<Member> membersOfType = new ArrayList<Member>();
		for(Member member : members) {
			if (!types.contains(member.participantRole())) membersOfType.add(member);
		}
		return membersOfType;
	}
	
	private void showMembersDialog() {
		if (isFinishing()) return;
		
		_pendingRecipientList = new ArrayList<Member>(_selectedRecipientList);
		
		ArrayList<String> memberNames = new ArrayList<String>();
		memberNames.add("Everyone");	// TODO : Make this better, Everyone/Team Only/Fans Only should be single select, rest multi select
		memberNames.add("Team Only");
		memberNames.add("Fans Only");
		for(Member member : _members) {
			memberNames.add(member.memberName());
		}
		
		// Build the list of prechecks
		boolean[] preChecked = new boolean[memberNames.size()];
		preChecked[0] = _pendingRecipientList.size() == _members.size();
		preChecked[1] = allMembersTeam(_pendingRecipientList);
		preChecked[2] = allMembersFans(_pendingRecipientList);
		for(int i=0; i < _members.size(); i++) {
			preChecked[i + 3] = _pendingRecipientList.contains(_members.get(i));
		}
		
		new AlertDialog.Builder(this)
				.setTitle("Select Members")
				.setMultiChoiceItems(memberNames.toArray(new String[memberNames.size()]), 
									 preChecked, 
									 new DialogInterface.OnMultiChoiceClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which, boolean isChecked) {
											selectMember(which, isChecked);
										}
									 })
				.setPositiveButton("Done", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) { updateRecipients(); }
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) { cancelRecipients(); }
				})
				.show();
	}
	
	private void selectMember(int index, boolean selected) {
		if (index >= 0 && index < _members.size() + 3) {
				switch(index) {
				case 0:	// Everyone
					_pendingRecipientList.clear();
					if (selected) _pendingRecipientList.addAll(_members);
					break;
				case 1: // Team Only
					_pendingRecipientList.clear();
					if (selected) _pendingRecipientList.addAll(getMembersTeam(_members));
					break;
				case 2: // Fans Only
					_pendingRecipientList.clear();
					if (selected) _pendingRecipientList.addAll(getMembersFans(_members));
					break;
				default:
					Member member = _members.get(index - 3);
					if (selected) 	_pendingRecipientList.add(member);
					else 			_pendingRecipientList.remove(member);
						break;
			}
		}
	}
	
	private void updateRecipients() {
		updateRecipientList(_pendingRecipientList);
	}
	
	private void cancelRecipients() {
		_pendingRecipientList = null;
		if(_clearOnSend) {
			_selectedTeam = null;
		}
	}

	
	
	//////////////////////////////////////////////////////////////
	/// Setting Associated Event
	
	protected void loadUpcomingEvents() {
		_allEvents = new ArrayList<EventBase>();
		_upcomingEvents = new ArrayList<EventBase>();
		EventLoader loader = new EventLoader(this, getSelectedTeam(), new EventLoader.EventLoaderCallback() {
			@Override 
			public void loading(boolean isLoading, String message) { 
				CustomTitle.setLoading(isLoading, message); 
			}
			
			@Override
			public void done(List<EventBase> eventsLoaded) {
				_allEvents.addAll(eventsLoaded);
				Date now = new Date();
				for(EventBase event : _allEvents) {
					if (event.startDate().after(now)) {
						_upcomingEvents.add(event);
					}
				}
				showEventsDialog();
			}
		});
		
		loader.load();
	}
	
	private void showEventsDialog() {
		CustomTitle.setLoading(false);
		if (isFinishing()) return;
		
		if (_upcomingEvents.size() == 0) {
			Toast.makeText(this, "There are no upcoming games or practices schedules.", Toast.LENGTH_SHORT).show();		
		}
		else {
			ArrayList<Simple3LineAdapater.Data> events = new ArrayList<Simple3LineAdapater.Data>();
			for(EventBase event : _upcomingEvents) {
				events.add(new Simple3LineAdapater.Data(DateUtils.toPrettyString(event.startDate()),
														event.eventType().toString() + " vs. " + event.opponent(),
														event.description()));
			}
			
			new AlertDialog.Builder(this)
				.setTitle("Choose an event:")
				.setAdapter(new Simple3LineAdapater(this, events, R.layout.list_item_simple3_white), new DialogInterface.OnClickListener() {
					@Override 
					public void onClick(DialogInterface dialog, int which) { setEvent(_upcomingEvents.get(which)); }
				})
				.show();
		}
	}
	
	private void setEvent(EventBase event) {
		_selectedEvent = event;
		updateEvent();
	}
	
	//////////////////////////////////////////////////////////////
	/// Cleanup
	
	protected void cleanup() {
		if (_clearOnSend) {
			setupTeam(null);
			setupEvent(null);
		}
	}
	
	//////////////////////////////////////////////////////////////
	//// Sending the message
	
	protected abstract NewMessageInfo getMessage();
	protected abstract void cleanupExtra();
	
	protected void sendMessage() {
		CustomTitle.setLoading(true, "Sending message...");
		final NewMessageInfo message = getMessage();
		MessageThreadsResource.instance().createMessage(getSelectedTeam().teamId(), message, new MessageThreadsResource.CreateMessageResponseHandler() {
			@Override public void finish(CreateMessageResponse response) { 
				sendMessageFinished(response);
				_tracker.trackMessageCreated(message);
			}
		});
	}
	
	private void sendMessageFinished(CreateMessageResponse response) {
		CustomTitle.setLoading(false);
		if (isFinishing()) return;
		
		if (response.getStatus() == ResponseStatus.Success) {
			clear();
			Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "Message failed to send. Please try again later.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void clear() {
		cleanup();
		cleanupExtra();
		initialize();
	}
}
