package com.rteam.android.teams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.ImageAdapater;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.TeamsResource;
import com.rteam.api.TeamsResource.CreateTeamResponse;
import com.rteam.api.business.Member;
import com.rteam.api.business.Team;
import com.rteam.api.common.StringUtils;

public class CreateTeam extends RTeamActivityChildTab {
	
	public interface CreateTeamHandler {
		public void onTeamCreated(Team newTeam);
	}
	
	private static CreateTeamHandler _handler;
	public static void setHandler(CreateTeamHandler handler) { _handler = handler; }
	public static void clearHandler() { setHandler(null); }
	private static CreateTeamHandler getHandler() { return _handler; }
	private static boolean hasHandler() { return getHandler() != null; }
	
	//////////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - create a team"; }
	
	private EditText _txtTeamName;
	private EditText _txtDescription;
	private EditText _txtTeamSport;
	private ToggleButton _btnUsesTwitter;
	private Button _btnCreateTeam;
	
	private View _dlgTeamSportView;
	private AlertDialog _dlgTeamSport;
	
	private Team.Sport _selectedSport;
	private GridView _gridSport;
	private EditText _txtOther;

	//////////////////////////////////////////////////////////////////////////
	//// Initialization	
	
	@Override
	protected void initialize() {
		initializeView();
	}
	
	private void initializeView() {
		setContentView(R.layout.teams_create);
		
		_txtTeamName = (EditText) findViewById(R.id.txtTeamName);
		_txtDescription = (EditText) findViewById(R.id.txtTeamDescription);
		_txtTeamSport = (EditText) findViewById(R.id.txtTeamSport);
		_btnUsesTwitter = (ToggleButton) findViewById(R.id.btnUsesTwitter);
		_btnCreateTeam = (Button) findViewById(R.id.btnCreate);
		
		_txtTeamSport.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { teamTypeClicked(); }
		});
				
		_btnCreateTeam.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { createTeamClicked(); }
		});
		
		_txtTeamName.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		_txtDescription.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		
		bindButtons();
	}
	
	private void initializeTeamSportDialog() {
		_dlgTeamSportView = getLayoutInflater().inflate(R.layout.dlg_teamsports, null);
		
		_gridSport = (GridView) _dlgTeamSportView.findViewById(R.id.gridSport);
		_txtOther = (EditText) _dlgTeamSportView.findViewById(R.id.txtOther);
		
		int[] buttons = new int[_teamSports.length];
		for(int i=0; i<_teamSports.length; i++) {
			buttons[i] = _teamSports[i].Resource;
		}
		_gridSport.setAdapter(new ImageAdapater(this, buttons, 72, 53));
		_gridSport.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				selectSport(_teamSports[position]);
			}
	    });
		
		bindTeamSportDialog();
	}
	
	private void bindTeamSportDialog() {
		if (_selectedSport != null && _selectedSport.isOther()) {
			_txtOther.setText(_selectedSport.toString());
		}
		else {
			_txtOther.setText("");
		}
	}
	
	private void bindTeamSport() {
		if (_selectedSport != null) {
			_txtTeamSport.setText(_selectedSport.toString());
		}
	}
	
	private void bindButtons() {
		_btnCreateTeam.setEnabled(StringUtils.hasText(_txtTeamName)
									&& StringUtils.hasText(_txtDescription)
									&& StringUtils.hasText(_txtTeamSport));
	}
	
	//////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	class SportHolder {
		public int Resource;
		public String Text;
		public Team.Sport Sport;
		
		public SportHolder(int resource, Team.Sport sport) {
			this(resource, sport.toString(), sport);
		}
		public SportHolder(int resource, String text, Team.Sport sport) {
			Resource = resource;
			Text = text;
			Sport = sport;
		}
	}
	
	private SportHolder[] _teamSports = new SportHolder[] {
		new SportHolder(R.drawable.teamtype_football, Team.Sport.Football),
		new SportHolder(R.drawable.teamtype_basketball, Team.Sport.Basketball),
		new SportHolder(R.drawable.teamtype_baseball, Team.Sport.Baseball),
		new SportHolder(R.drawable.teamtype_soccer, Team.Sport.Soccer),
		new SportHolder(R.drawable.teamtype_hockey, Team.Sport.Hockey),
		new SportHolder(R.drawable.teamtype_lacrosse, Team.Sport.Lacrosse),
		new SportHolder(R.drawable.teamtype_tennis, Team.Sport.Tennis),
		new SportHolder(R.drawable.teamtype_volleyball, Team.Sport.Volleyball)
	};
	
	private void teamTypeClicked() {
		initializeTeamSportDialog();
		
		_dlgTeamSport = 
			new AlertDialog.Builder(this)
			.setView(_dlgTeamSportView)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override 
				public void onClick(DialogInterface dialog, int which) {
					teamTypeDone();
				} 
			}).show();
	}
	
	private void selectSport(SportHolder sport) {
		_dlgTeamSport.dismiss();
		selectTeamSport(sport.Sport);
	}
	
	private void selectTeamSport(Team.Sport sport) {
		_selectedSport = sport;
		bindTeamSport();
		bindButtons();
	}
	
	private void teamTypeDone() {
		selectTeamSport(new Team.Sport(_txtOther.getText().toString()));
	}
	
	
	private void createTeamClicked() {
		CustomTitle.setLoading(true, "Creating...");
		final Team newTeam = getTeam();
		TeamsResource.instance().createTeam(newTeam, new TeamsResource.CreateTeamResponseHandler() {
			@Override public void finish(CreateTeamResponse response) {
				if (response.showError(CreateTeam.this)) {
					teamCreated(newTeam, response.getTwitterAuthorizationUrl());
				}
			}
		});
	}
	
	private void teamCreated(final Team newTeam, String twitterAuthUrl) {
		CustomTitle.setLoading(false);
		TeamCache.put(newTeam);
		
		if (!StringUtils.isNullOrEmpty(twitterAuthUrl)) {
			AuthorizeTwitter.setup(newTeam, twitterAuthUrl, new AuthorizeTwitter.Callback() {
				@Override
				public void doneAuthorizing() {
					handleTeamCreated(newTeam);
				}
			});
			startActivity(new Intent(this, AuthorizeTwitter.class));
		}
		else {
			handleTeamCreated(newTeam);
		}
	}
	
	private void handleTeamCreated(Team newTeam) {
		if (newTeam != null) {
			if (hasHandler()) {
				getHandler().onTeamCreated(newTeam);
			}
			clearHandler();
			finish();
			
			TeamDetails.setTeam(newTeam);
			startActivity(new Intent(this, TeamDetails.class));
		}
	}
	
	private Team getTeam() {
		Team team = new Team();
		team.participantRole(Member.Role.Creator);
		team.teamName(_txtTeamName.getText().toString());
		team.description(_txtDescription.getText().toString());
		team.sport(_selectedSport);
		team.usesTwitter(_btnUsesTwitter.isChecked());
		return team;
	}
}