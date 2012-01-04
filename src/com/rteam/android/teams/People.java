package com.rteam.android.teams;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.people.CreateMember;
import com.rteam.android.people.EditMember;
import com.rteam.android.people.InviteAFan;
import com.rteam.android.teams.common.PeopleListAdapater;
import com.rteam.api.MembersResource;
import com.rteam.api.MembersResource.MemberListResponse;
import com.rteam.api.business.Member;
import com.rteam.api.business.Member.Role;
import com.rteam.api.business.Team;

public class People extends RTeamActivityChildTab {

	////////////////////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - team members/fans"; }
	
	private ExpandableListView _listPeople;
	private View _barBottom;
	private Button _btnAddMember;
	private Button _btnInviteFan;
	
	private ArrayList<Member> _people;
	
	private ArrayList<Member> _members;
	private ArrayList<Member> _fans;
	
	private Team getTeam() { return TeamDetails.getTeam(); }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows the current list of members and fans on the current team.  Allows managers to add members or invite fans."));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//// View initialization
	
	@Override
	protected void initialize() {
		initializeView();
		loadMemberData();
	}
	
	private void initializeView() {
		setContentView(R.layout.teams_people);
		
		_listPeople = (ExpandableListView) findViewById(R.id.listPeople);
		_barBottom = findViewById(R.id.barBottom);
		_btnAddMember = (Button) findViewById(R.id.btnAddMember);
		_btnInviteFan = (Button) findViewById(R.id.btnInvite);
		
		_btnAddMember.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { addMemberClicked(); }
		});
		
		_btnInviteFan.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { inviteFanClicked(); }
		});
	}
	
	private void bindView() {
		_barBottom.setVisibility(getTeam().participantRole().atLeast(Role.Fan) ? View.VISIBLE : View.GONE);
		_btnAddMember.setEnabled(getTeam().participantRole().atLeast(Role.Coordinator));
				
		_listPeople.setAdapter(new PeopleListAdapater(this, _members, _fans));
		_listPeople.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupIndex, int index, long id) {
				switch (groupIndex) {
				case 0:
					memberClick(_members.get(index));
					return true;
				case 1:
					memberClick(_fans.get(index));
					return true;
				}
				return false;
			}
		});
		for (int i=0; i<_listPeople.getExpandableListAdapter().getGroupCount(); i++) {
			_listPeople.expandGroup(i);
		}
	}
	
	private void memberClick(Member member) {		
		EditMember.setupMember(member, new EditMember.MemberUpdated() {
			@Override
			public void onMemberUpdate(Member updatedMember) {
				updatedMember.bindTeam(getTeam());
				int existingIndex = -1;
				for(int i = 0; i < _members.size(); i++) {
					Member member = _members.get(i);
					if(member.memberId().equalsIgnoreCase(updatedMember.memberId())) {
						existingIndex = i;
						break;
					}
				}
				
				if(existingIndex == -1) {
					_members.add(updatedMember);
				}
				else {
					_members.remove(existingIndex);
					_members.add(existingIndex, updatedMember);
				}

				bindView();
				
				loadMemberData();
			}
		});		
		startActivity(new Intent(this, EditMember.class));
	}
	
	private void addMemberClicked() {
		CreateMember.setup(getTeam(), new CreateMember.MemberCreated() {
			@Override
			public void onMemberCreate(Member newMember) {
				newMember.bindTeam(getTeam());
				_members.add(newMember);
				bindView();
				
				loadMemberData();
			}
		});
		
		startActivity(new Intent(this, CreateMember.class));
	}
	
	private void inviteFanClicked() {
		InviteAFan.setup(getTeam(), new InviteAFan.FanAdded() {
			@Override
			public void onFanAdd(Member newFan) {
				_fans.add(newFan);
				bindView();	// TODO : Do this better!! shouldn't re-bind the entire frickin list
				
				loadMemberData();
			}
		});
		
		startActivity(new Intent(this, InviteAFan.class));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//// Data loading

	private void loadMemberData() {
		CustomTitle.setLoading(true, "Loading...");
		MembersResource.instance().getMembers(getTeam().teamId(), true, new MembersResource.MemberListResponseHandler() {
			@Override public void finish(MemberListResponse response) { loadMembersFinished(response); }
		});
	}
	
	private void loadMembersFinished(MemberListResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_people = response.members();
			
			_members = new ArrayList<Member>();
			_fans = new ArrayList<Member>();
			for(Member member : _people) {
				member.bindTeam(getTeam());
				if (member.participantRole() == Member.Role.Fan) {
					_fans.add(member);
				}
				else {
					_members.add(member);
				}
			}
			
			bindView();
		}
	}
}