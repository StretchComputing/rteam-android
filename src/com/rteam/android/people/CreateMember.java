package com.rteam.android.people;

import java.util.ArrayList;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.rteam.android.R;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.HelpProvider.HelpContent;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.people.common.GuardiansDialog;
import com.rteam.api.MembersResource;
import com.rteam.api.MembersResource.CreateMemberResponse;
import com.rteam.api.business.Member;
import com.rteam.api.business.Member.Guardian;
import com.rteam.api.business.Team;
import com.rteam.api.common.ArrayListUtils;
import com.rteam.api.common.StringUtils;

public class CreateMember extends RTeamActivity {
	
	public interface MemberCreated {
		public void onMemberCreate(Member newMember);
	}
	
	////////////////////////////////////////////////////////////////////
	//// Static Members
	
	private static MemberCreated _handler;
	public static void setHandler(MemberCreated handler) { _handler = handler; }
	private static MemberCreated getHandler() { return _handler; }
	private static boolean hasHandler() { return _handler != null; }
	
	private static Team _team;
	public static void setTeam(Team team) { _team = team; }
	private static Team getTeam() { return _team; }
	
	public static void setup(Team team, MemberCreated handler) {
		setTeam(team);
		setHandler(handler);
	}
	public static void clear() { setup(null, null); }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpContent("Overview", "Creates a new member for the current team."));
	}
	
	
	////////////////////////////////////////////////////////////////////
	//// Members
	
	@Override
	protected String getCustomTitle() { return "rTeam - create member"; }
	
	private EditText _txtFirstName;
	private EditText _txtLastName;
	private EditText _txtEmail;
	private EditText _txtPhoneNumber;
	private EditText _txtGuardians;
	private ToggleButton _btnCoordinator;
	private Button _btnCreate;
		
	private ArrayList<Member.Guardian> _guardians = new ArrayList<Member.Guardian>();
	
	////////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
	}
	
	private void initializeView() {
		setContentView(R.layout.people_create_member);
		
		_txtFirstName = (EditText) findViewById(R.id.txtFirstName);
		_txtLastName = (EditText) findViewById(R.id.txtLastName);
		_txtEmail = (EditText) findViewById(R.id.txtEmail);
		_txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
		_txtGuardians = (EditText) findViewById(R.id.txtGuardians);
		
		_btnCoordinator = (ToggleButton) findViewById(R.id.btnCoordinator);
		_btnCreate = (Button) findViewById(R.id.btnCreate);
						
		_txtGuardians.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setGuardians(); }
		});
		
		_btnCreate.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { createMemberClicked(); }
		});
		
		
		_txtFirstName.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		_txtLastName.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		_txtEmail.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		_txtPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		bindButtons();
	}
	
	private void bindView() {
		_txtGuardians.setText(ArrayListUtils.toString(_guardians, ";", new ArrayListUtils.GetString<Member.Guardian>() {
			@Override public String getString(Guardian obj) { return obj.firstName() + " " + obj.lastName(); } 
		}));
	}
	
	private void bindButtons() {
		_btnCreate.setEnabled(StringUtils.hasText(_txtFirstName)
								&& StringUtils.hasText(_txtLastName)
								&& (StringUtils.hasText(_txtEmail) || StringUtils.hasText(_txtPhoneNumber)));
	}
		
	////////////////////////////////////////////////////////////////////
	//// Event Handlers

	
	private void setGuardians() {
		if (isFinishing()) return;
		
		new GuardiansDialog(this, _guardians, new GuardiansDialog.SetGuardiansHandler() {
			@Override
			public void setGuardians(ArrayList<Guardian> guardians) {
				_guardians = guardians;
				bindView();
			}
		}).showDialog();
	}
	
	private void createMemberClicked() {
		final Member newMember = getMember();
		MembersResource.instance().create(newMember, new MembersResource.CreateMemberResponseHandler() {
			@Override
			public void finish(CreateMemberResponse response) {
				if (response.showError(CreateMember.this)) {
					createMemberFinished(newMember);
				}
			}
		});
	}
	
	private void createMemberFinished(Member newMember) {
		if (hasHandler()) {
			getHandler().onMemberCreate(newMember);
		}
		clear();
		finish();
	}
	
	////////////////////////////////////////////////////////////////////
	//// Helpers
	
	private Member getMember() {
		Member member = new Member(getTeam().teamId(), _txtFirstName.getText().toString(), _txtLastName.getText().toString(), _txtEmail.getText().toString());
		member.phoneNumber(_txtPhoneNumber.getText().toString());
		member.participantRole(_btnCoordinator.isChecked() ? Member.Role.Coordinator : Member.Role.Member);
		member.guardians(_guardians);
		return member;
	}
}

