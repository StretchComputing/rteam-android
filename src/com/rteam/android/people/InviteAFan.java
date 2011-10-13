package com.rteam.android.people;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.teams.common.TeamSelectDialog;
import com.rteam.api.MembersResource;
import com.rteam.api.MembersResource.CreateMemberResponse;
import com.rteam.api.business.Member;
import com.rteam.api.business.Team;
import com.rteam.api.business.Member.Role;

public class InviteAFan extends RTeamActivityChildTab {

	public interface FanAdded {
		public void onFanAdd(Member newFan);
	}
	
	///////////////////////////////////////////////////////////
	//// Static Members
	
	private static Team _team;
	public static void setTeam(Team team) { _team = team; }
	private static Team getTeam() { return _team; }
	private static boolean hasTeam() { return getTeam() != null; }
	
	private static FanAdded _fanAdded;
	public static void setFanAdded(FanAdded handler) { _fanAdded = handler; }
	private static FanAdded getFanAdded() { return _fanAdded; }
	private static boolean hasFanAdded() { return getFanAdded() != null; }
	
	public static void setup(Team team, FanAdded handler) {
		setTeam(team);
		setFanAdded(handler);
	}
	public static void clear() {
		setup(null, null);
	}
	
	///////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - invite a fan"; }
	
	private EditText _txtFirstName;
	private EditText _txtLastName;
	private EditText _txtEmail;
	
	private Button _btnInvite;
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Invites a fan to a team."));
	}
		
	@Override
	protected void initialize() {
		initializeView();
		promptForTeam();
	}

	
	private void initializeView() {
		setContentView(R.layout.people_invite_fan);
		
		_txtFirstName = (EditText) findViewById(R.id.txtFirstName);
		_txtLastName = (EditText) findViewById(R.id.txtLastName);
		_txtEmail = (EditText) findViewById(R.id.txtEmail);
		
		_btnInvite = (Button) findViewById(R.id.btnInvite);
		
		_txtEmail.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				bindView();
				return false;
			}
		});
		
		_btnInvite.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { inviteFan(); }
		});
	}
	
	private void promptForTeam() {
		if (hasTeam()) return;
		loadTeams();
	}
	
	private void bindView() {
		_btnInvite.setEnabled(hasValidEmail());
	}
	
	private boolean hasValidEmail() {
		// TODO : Probably some validation
		return _txtEmail.getText().length() > 0;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////
	//// Loading Teams
	
	private void loadTeams() {
		new TeamSelectDialog(this, new TeamSelectDialog.TeamSelectHandler() {
			@Override 
			public void teamSelected(Team team) {
				if (team != null) setTeam(team);
				else finish();
			}
		});
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	//// Event Handlers	
		
	private void inviteFan() {
		final Member fan = getFan();
		CustomTitle.setLoading(true, "Savings...");
		MembersResource.instance().create(fan, new MembersResource.CreateMemberResponseHandler() {
			@Override
			public void finish(CreateMemberResponse response) {
				inviteFanFinished(fan);
			}
		});
	}
	
	private void inviteFanFinished(Member newFan) {
		CustomTitle.setLoading(false);
		if (hasFanAdded()) {
			FanAdded handler = getFanAdded();
			handler.onFanAdd(newFan);
		}
		clear();
		finish();
		Toast.makeText(this, "Saved Fan, an email has been sent to the user inviting them to follow this team.", Toast.LENGTH_SHORT).show();
	}
	
	private Member getFan() {
		Member member = new Member(getTeam().teamId(), 
						  			_txtFirstName.getText().toString(),
					  				_txtLastName.getText().toString(),
				  					_txtEmail.getText().toString());
		member.participantRole(Role.Fan);
		return member;
	}
}
