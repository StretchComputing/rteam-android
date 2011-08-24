package com.rteam.android.teams.common;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.rteam.android.common.CustomTitle;
import com.rteam.api.TeamsResource;
import com.rteam.api.TeamsResource.TeamListResponse;
import com.rteam.api.business.Team;

public class TeamSelectDialog {

	public interface TeamSelectHandler {
		public void teamSelected(Team team);
	}
	
	private Context _context;
	private TeamSelectHandler _selectHandler;
	private ArrayList<Team> _availableTeams;
	private Team _selectedTeam;
	
	public TeamSelectDialog(Context context, TeamSelectHandler selectHandler) {
		_context = context;
		_selectHandler = selectHandler;
		loadTeams();
	}
	
	private void loadTeams() {
		CustomTitle.setLoading(true, "Loading teams...");
		new TeamsResource().getTeams(new TeamsResource.TeamListResponseHandler() {
			@Override public void finish(TeamListResponse response) { loadTeamsFinished(response); }
		});
	}
	
	private void loadTeamsFinished(TeamListResponse response) {
		_availableTeams = response.teams();
		
		ArrayList<String> teams = new ArrayList<String>();
		for(Team team : _availableTeams) {
			teams.add(team.teamName());
		}
		
		CustomTitle.setLoading(false);
		
		new AlertDialog.Builder(_context)
				.setSingleChoiceItems(teams.toArray(new String[teams.size()]), -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						_selectedTeam = _availableTeams.get(which);
					}
				})
				.setPositiveButton("Select Team", new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { setSelectedTeam(_selectedTeam); }
				})
				.setNegativeButton("Cancel", new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { setSelectedTeam(null); }
				})
				.show();
	}
	
	private void setSelectedTeam(Team team) {
		if (_selectHandler != null) {
			_selectHandler.teamSelected(team);
		}
	}
}
