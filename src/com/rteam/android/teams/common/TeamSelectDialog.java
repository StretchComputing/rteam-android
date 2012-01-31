package com.rteam.android.teams.common;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.rteam.android.common.CustomTitle;
import com.rteam.api.business.Team;

public class TeamSelectDialog {

	public interface TeamSelectHandler {
		public void teamSelected(Team team);
	}
	
	private Context _context;
	private TeamSelectHandler _selectHandler;
	private List<Team> _availableTeams;
	private Team _selectedTeam;
	private String _title;
	
	public TeamSelectDialog(Context context, TeamSelectHandler selectHandler) {
		this(context, "Select a Team", selectHandler);
	}
	
	public TeamSelectDialog(Context context, String title, TeamSelectHandler selectHandler) {
		_context = context;
		_selectHandler = selectHandler;
		_title = title;
		loadTeams();
	}
	
	private void loadTeams() {
		CustomTitle.setLoading(true, "Loading teams...");
		_availableTeams = TeamCache.getTeams();
		
		List<String> teams = new ArrayList<String>();
		for(Team team : _availableTeams) {
			teams.add(team.teamName());
		}
		
		CustomTitle.setLoading(false);
		
		new AlertDialog.Builder(_context)
				.setTitle(_title)
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
