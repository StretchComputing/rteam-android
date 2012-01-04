package com.rteam.android.people;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
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
import com.rteam.api.common.StringUtils;

public class EditMember extends RTeamActivity {
	
	////////////////////////////////////////////////////////////////
	//// Helper Class
	
	public interface MemberUpdated {
		public void onMemberUpdate(Member updatedMember);
	}
	
	////////////////////////////////////////////////////////////////
	//// Members
	
	private static MemberUpdated _memberUpdated;
	private static Member _origMember;
	public static void setupMember(Member member, MemberUpdated memberUpdated) { 
		_origMember = member; 
		_memberUpdated = memberUpdated;
	}
	private Member _member;
	
	@Override
	protected String getCustomTitle() { return "rTeam - edit member"; }
	
	private boolean isFan() { return _origMember.participantRole() == Role.Fan; }
	private Team getTeam() { return _member != null ? TeamCache.get(_member.teamId()) : null; }
	private boolean canEdit() {
		Team team = getTeam();
		return isFullyCreated() && ((team != null && team.participantRole() != null) ? team.participantRole().atLeast(Role.Coordinator) : false);
	}
	private boolean isFullyCreated() { return !StringUtils.isNullOrEmpty(_origMember.memberId()); }
	
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
		
		if(canEdit()) {
			_txtFirstName.setOnKeyListener(new View.OnKeyListener() {
				@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
			});
			_txtLastName.setOnKeyListener(new View.OnKeyListener() {
				@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
			});
			_txtEmailAddress.setOnKeyListener(new View.OnKeyListener() {
				@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
			});
			_txtPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
				@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
			});
		}
		
		_txtJerseyNumber.setVisibility(isFan() ? View.GONE : View.VISIBLE);
		_txtGuardians.setVisibility(isFan() ? View.GONE : View.VISIBLE);
		
		bindInputs();
	}
	
	private void bindView() {
		_txtFirstName.setText(_member.firstName());
		_txtLastName.setText(_member.lastName());
		_txtJerseyNumber.setText(_member.jerseyNumber());
		_txtEmailAddress.setText(_member.emailAddress());
		_txtPhoneNumber.setText(_member.phoneNumber());
		
		bindInputs();
		bindGuardians();
		bindImage();
		bindButtons();
	}
	
	private void bindInputs() {
		boolean canEdit = canEdit();
		String saveButtonText = canEdit ? "Save Changes" : "Done";
		boolean enabled = canEdit && _member != null;
		
		_btnSave.setText(saveButtonText);
		_txtFirstName.setEnabled(enabled);
		_txtLastName.setEnabled(enabled);
		_txtJerseyNumber.setEnabled(enabled);
		_txtEmailAddress.setEnabled(enabled);
		_txtPhoneNumber.setEnabled(enabled);
		_txtGuardians.setEnabled(enabled);
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
	
	private void bindButtons() {
		if(canEdit()) {
			_btnSave.setEnabled(StringUtils.hasText(_txtFirstName)
					&& StringUtils.hasText(_txtLastName)
					&& (StringUtils.hasText(_txtEmailAddress) || StringUtils.hasText(_txtPhoneNumber)));
		}
	}	
	
	///////////////////////////////////////////////////////////////////////////////////
	//// Loading Data
	
	private void loadMember() {
		if (isFullyCreated()) {
			CustomTitle.setLoading(true, "Loading member info...");
			MembersResource.instance().getFullMember(_origMember, true, new GetMemberResponseHandler() {			
				@Override public void finish(GetMemberResponse response) { loadMemberFinished(response); }
			});
		}
		else {
			bindView();
		}
	}
	
	private void loadMemberFinished(GetMemberResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			_member = response.member();
			bindView();
		}
		else {
			finish();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////
	//// Event Handlers
	
	private static int TAKE_PICTURE = 1;
	private void memberImageClicked() {
		if (isFinishing() || _member == null) {
			return;
		}
		
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
		if (isFinishing() || _member == null) {
			return;
		}
		
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
			_btnSave.setEnabled(false);
			MembersResource.instance().updateMember(_member, new MembersResource.UpdateMemberResponseHandler() {
				@Override public void finish(UpdateMemberResponse response) { saveMemberFinished(response); }
			});
		}
		else {
			finish();
		}
	}
	
	private void saveMemberFinished(UpdateMemberResponse response) {
		CustomTitle.setLoading(false);
		_btnSave.setEnabled(true);
		if (response.showError(this)) {
			Toast.makeText(this, "Successfully saved member.", Toast.LENGTH_SHORT).show();
			
			_newMemberImage = null;
			_newGuardians = null;
			_memberUpdated.onMemberUpdate(_member);
			
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
