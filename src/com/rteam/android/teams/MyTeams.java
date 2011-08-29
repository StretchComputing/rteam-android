package com.rteam.android.teams;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.common.SimpleListAdapter;
import com.rteam.android.common.SimpleListGroup;
import com.rteam.android.common.SimpleListItem;
import com.rteam.api.TeamsResource;
import com.rteam.api.TeamsResource.TeamListResponse;
import com.rteam.api.business.Member;
import com.rteam.api.business.Team;

public class MyTeams extends RTeamActivity {
	
	////////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - my teams"; }
	
	private Button _createTeam;
	
	private ExpandableListView _listTeams;
		
	private ArrayList<Team> _myTeams;
	
	private ArrayList<Team> _myTeamsMemberOf;
	private ArrayList<Team> _myTeamsFanOf;
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Shows you a list of the teams that you are a member of, as well as a list of teams that you are a fan of."),
							    new HelpContent("Create Team", "Allows you to create a new team to manage."));
	}
	
    @Override
    protected void initialize() {
    	initializeView();
    	loadTeams();
    }
    
    private static boolean isValidIndex(ArrayList<Team> list, int index) {
    	return list != null && index >= 0 && index < list.size();
    }
    
    private void initializeView()     {
    	setContentView(R.layout.teams_myteams);
    	
    	_listTeams = (ExpandableListView) findViewById(R.id.listTeams);    	
    	_createTeam = (Button) findViewById(R.id.btnCreateTeam);
    	
    	
    	_listTeams.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				if (groupPosition == 0 && isValidIndex(_myTeamsMemberOf, childPosition)) {
					teamClicked(_myTeamsMemberOf.get(childPosition));
				}
				else if(groupPosition == 1 && isValidIndex(_myTeamsFanOf, childPosition)) {
					teamClicked(_myTeamsFanOf.get(childPosition));
				}
				return false;
			}
		}); 
    	
    	_createTeam.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) { createTeamClicked(); }
    	});    	
    }
    
    
    
    private void bindView() { 
    	ArrayList<SimpleListGroup> groups = new ArrayList<SimpleListGroup>();
    	ArrayList<SimpleListItem> memberOfItems = new ArrayList<SimpleListItem>();
    	ArrayList<SimpleListItem> fanOfItems = new ArrayList<SimpleListItem>();
    	for (Team team : _myTeamsMemberOf) {
    		memberOfItems.add(new SimpleListItem(team.teamName(), null));
    	}
    	for (Team team : _myTeamsFanOf) {
    		fanOfItems.add(new SimpleListItem(team.teamName(), null));
    	}
    	
    	if (memberOfItems.size() == 0) {
    		memberOfItems.add(new SimpleListItem("You are not a member of any teams.", null));
    	}
    	if (fanOfItems.size() == 0) {
    		fanOfItems.add(new SimpleListItem("You are not a fan of any teams.", null));
    	}
    	
    	groups.add(new SimpleListGroup("Member Of:", memberOfItems));
    	groups.add(new SimpleListGroup("Fan Of:", fanOfItems));
    	
    	_listTeams.setAdapter(new SimpleListAdapter(this, groups));
    	for (int i=0; i<groups.size(); i++) {
    		_listTeams.expandGroup(i);
    	}
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Click Listeners
    
    private void teamClicked(Team team) {
    	TeamDetails.setTeam(team);
    	startActivity(new Intent(this, TeamDetails.class));
    }
    
    private void createTeamClicked() {
    	CreateTeam.setHandler(new CreateTeam.CreateTeamHandler() {
			@Override
			public void onTeamCreated(Team newTeam) {
				_myTeams.add(newTeam);
				_myTeamsMemberOf.add(newTeam);
				bindView();
			}
		});
    	startActivity(new Intent(this, CreateTeam.class));
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Loading Teams
    
    private void loadTeams() {
    	CustomTitle.setLoading(true, "Loading teams...");
    	new TeamsResource().getTeams(new TeamsResource.TeamListResponseHandler() {
			@Override public void finish(TeamListResponse response) { loadTeamsFinished(response); }
    	});
    }
    
    private void loadTeamsFinished(TeamListResponse response) {
    	CustomTitle.setLoading(false);
    	if (response.showError(this)) {
	    	_myTeams = response.teams();
	    	
	    	_myTeamsMemberOf = new ArrayList<Team>();
	    	_myTeamsFanOf = new ArrayList<Team>();
	    	for(Team team : _myTeams) {
	    		if (team.participantRole() == Member.Role.Fan) {
	    			_myTeamsFanOf.add(team);
	    		}
	    		else {
	    			_myTeamsMemberOf.add(team);
	    		}
	    	}
	    	bindView();
    	}
    }
}
