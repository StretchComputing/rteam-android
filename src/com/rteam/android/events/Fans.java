package com.rteam.android.events;

import java.util.ArrayList;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.events.common.FanListAdapter;
import com.rteam.api.MembersResource;
import com.rteam.api.MembersResource.MemberListResponse;
import com.rteam.api.business.Member;

public class Fans extends RTeamActivityChildTab {
	
	///////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - event fans"; }
	
	private ListView _listFans;
	
	private ArrayList<Member> _fans;

	///////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected void initialize() {
		initializeView();
		loadFans();
	}
	
	private void initializeView() {
		setContentView(R.layout.events_fans);
		
		_listFans = (ListView) findViewById(R.id.listFans);
		_listFans.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				// TODO : Implement this!
			}			
		});
	}
	
	private void bindView() {
		_listFans.setAdapter(new FanListAdapter(this, _fans));
	}
	
	///////////////////////////////////////////////////////////////
	//// Load Data
	private void loadFans() {
		CustomTitle.setLoading(true, "Loading fans...");
		MembersResource.instance().getMembers(EventDetails.getTeam().teamId(), true, new MembersResource.MemberListResponseHandler() {
			@Override
			public void finish(MemberListResponse response) { loadFansFinished(response); }
		});
	}
	
	private void loadFansFinished(MemberListResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_fans = new ArrayList<Member>();
			for(Member member : response.members()) {
				if (member.participantRole() == Member.Role.Fan) {
					_fans.add(member);
				}
			}
			bindView();
		}
	}
}
