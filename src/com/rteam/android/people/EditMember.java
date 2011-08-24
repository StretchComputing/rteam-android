package com.rteam.android.people;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.messaging.SendMessage;
import com.rteam.android.people.common.GuardiansDialog;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.MembersResource;
import com.rteam.api.MembersResource.GetMemberResponse;
import com.rteam.api.MembersResource.GetMemberResponseHandler;
import com.rteam.api.MembersResource.UpdateMemberResponse;
import com.rteam.api.business.Member;
import com.rteam.api.business.Member.Guardian;
import com.rteam.api.business.Member.Role;
import com.rteam.api.business.Team;
import com.rteam.api.common.ArrayListUtils;
import com.rteam.api.common.ObjectUtils;

public class EditMember extends RTeamActivity {
	
	////////////////////////////////////////////////////////////////
	//// Members
	
	private static Member _member;
	public static void setupMember(Member member) { _member = member; }
	
	@Override
	protected String getCustomTitle() { return "rTeam - edit member"; }
	
	private boolean isFan() { return _member.participantRole() == Role.Fan; }
	private boolean canEdit() { return getTeam().participantRole().atLeast(Role.Coordinator); }
	private Team getTeam() { return TeamCache.get(_member.teamId()); }
	
	private ImageView _memberImage;
	private EditText _txtFirstName;
	private EditText _txtLastName;
	private EditText _txtJerseyNumber;
	private EditText _txtEmailAddress;
	private EditText _txtPhoneNumber;
	private EditText _txtGuardians;
	
	private Button _btnSave;
	
	private Button _btnSendMessage;
	
	private Bitmap _newMemberImage = null;
	
	private ArrayList<Member.Guardian> _newGuardians = null;
	
	private Bitmap getMemberImage() { 
		return _newMemberImage != null 
					? _newMemberImage 
					: _member.memberImage(); 
	}
		
	private ArrayList<Member.Guardian> getGuardians() {
		return _newGuardians != null
					? _newGuardians
					: _member.guardians();
	}

	
	////////////////////////////////////////////////////////////////
	//// Initialization
	
	@Override
	protected void initialize() {
		initializeView();
		loadMember();
	}
	
	private void initializeView() {
		setContentView(R.layout.people_edit_member);
		
		_memberImage = (ImageView) findViewById(R.id.imageMember);
		_txtFirstName = (EditText) findViewById(R.id.txtFirstName);
		_txtLastName = (EditText) findViewById(R.id.txtLastName);
		_txtJerseyNumber = (EditText) findViewById(R.id.txtJerseyNumber);
		_txtEmailAddress = (EditText) findViewById(R.id.txtEmail);
		_txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
		_txtGuardians = (EditText) findViewById(R.id.txtGuardians);
		
		_btnSave = (Button) findViewById(R.id.btnSave);
		_btnSendMessage = (Button) findViewById(R.id.btnSend);	
		
		_txtGuardians.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v) { guardiansClicked(); }
		});
		
		_memberImage.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { memberImageClicked(); }
		});
		
		_btnSave.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { saveMemberClicked(); }
		});
		
		_btnSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { sendMessageToMember(); }
		});
				
		_txtJerseyNumber.setVisibility(isFan() ? View.INVISIBLE : View.VISIBLE);
		_txtGuardians.setVisibility(isFan() ? View.INVISIBLE : View.VISIBLE);
	}
	
	private void bindView() {
		_txtFirstName.setText(_member.firstName());
		_txtLastName.setText(_member.lastName());
		_txtJerseyNumber.setText(_member.jerseyNumber());
		_txtEmailAddress.setText(_member.emailAddress());
		_txtPhoneNumber.setText(_member.phoneNumber());
		
		if (canEdit()) {
			_btnSave.setText("Save Changes");
			_txtFirstName.setEnabled(true);
			_txtLastName.setEnabled(true);
			_txtJerseyNumber.setEnabled(true);
			_txtEmailAddress.setEnabled(true);
			_txtPhoneNumber.setEnabled(true);
			_txtGuardians.setEnabled(true);
		}
		else {
			_btnSave.setText("Done");
			_txtFirstName.setEnabled(false);
			_txtLastName.setEnabled(false);
			_txtJerseyNumber.setEnabled(false);
			_txtEmailAddress.setEnabled(false);
			_txtPhoneNumber.setEnabled(false);
			_txtGuardians.setEnabled(false);
		}
		
		bindGuardians();
		bindImage();
	}
	
	private void bindGuardians() {
		if (getGuardians() != null) 
			_txtGuardians.setText(ArrayListUtils.toString(getGuardians(), ";", new ArrayListUtils.GetString<Member.Guardian>() {
				@Override public String getString(Guardian obj) { return obj.firstName() + " " + obj.lastName(); } 
			}));
	}
	
	private void bindImage() {
		if (getMemberImage() != null) {
			_memberImage.setImageBitmap(getMemberImage());
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	//// Loading Data
	
	private void loadMember() {
		CustomTitle.setLoading(true, "Loading member info...");
		new MembersResource().getFullMember(_member, true, new GetMemberResponseHandler() {			
			@Override public void finish(GetMemberResponse response) { loadMemberFinished(response); }
		});
	}
	
	private void loadMemberFinished(GetMemberResponse response) {
		_member = response.member();
		bindView();
		CustomTitle.setLoading(false);
	}

	//////////////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private static int TAKE_PICTURE = 1;
	private void memberImageClicked() {
		if (getMemberImage() != null) {
			ImageView image = new ImageView(this);
			image.setImageBitmap(getMemberImage());
			
			new AlertDialog.Builder(this)
				.setTitle(_member.firstName() + " " + _member.lastName() + " profile picture")
				.setView(image)
				.setNeutralButton("Change", new OnClickListener() {
					@Override public void onClick(DialogInterface arg0, int arg1) { takeNewMemberPicture(); }
				})
				.setNegativeButton("Close", null)
				.show();
		}
		else {
			takeNewMemberPicture();
		}
	}
		
	private void takeNewMemberPicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1000000);
		startActivityForResult(takePictureIntent, TAKE_PICTURE);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {			
			if (data != null && data.hasExtra("data")) {
				_newMemberImage = data.getParcelableExtra("data");
				bindImage();
			}
		}
	}
	
	private void guardiansClicked() {
		new GuardiansDialog(this, getGuardians(), new GuardiansDialog.SetGuardiansHandler() {
			@Override
			public void setGuardians(ArrayList<Guardian> guardians) {
				_newGuardians = guardians;
				bindGuardians();
			}
		}).showDialog();
	}
	
	private void sendMessageToMember() {
		SendMessage.setupSendMessageTo(_member);
		startActivity(new Intent(this, SendMessage.class));
	}

	private void saveMemberClicked() {
		if (canEdit()) {
			_member = getMember();
			CustomTitle.setLoading(true, "Saving member...");
			new MembersResource().updateMember(_member, new MembersResource.UpdateMemberResponseHandler() {
				@Override public void finish(UpdateMemberResponse response) { saveMemberFinished(response); }
			});
		}
		else {
			finish();
		}
	}
	
	private void saveMemberFinished(UpdateMemberResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_newMemberImage = null;
			_newGuardians = null;
			
			bindView();
		}
	}

	private Member getMember() {
		Member member = new Member(_member);
		member.memberId(_member.memberId());
		member.firstName(_txtFirstName.getText().toString());
		member.lastName(_txtLastName.getText().toString());
		member.memberName(member.firstName() + " " + member.lastName());
		member.emailAddress(_txtEmailAddress.getText().toString());
		member.phoneNumber(_txtPhoneNumber.getText().toString());
		member.jerseyNumber(_txtJerseyNumber.getText().toString());
		member.guardians(ObjectUtils.notNull(_newGuardians, _member.guardians()));
		member.memberImage(ObjectUtils.notNull(_newMemberImage, member.memberImage()));
		return member;
	}
}
