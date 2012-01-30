package com.rteam.android.people.common;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.rteam.android.R;
import com.rteam.api.business.Member;
import com.rteam.api.common.AndroidUtils;

public class GuardiansDialog {
	
	public interface SetGuardiansHandler {
		public void setGuardians(ArrayList<Member.Guardian> guardians);
	}

	private SetGuardiansHandler _doneHandler;
	private LayoutInflater _layoutInflater;

	private View _guardianView;
	private EditText _txtGuardianFirstName1;
	private EditText _txtGuardianLastName1;
	private EditText _txtGuardianEmail1;
	private EditText _txtGuardianFirstName2;
	private EditText _txtGuardianLastName2;
	private EditText _txtGuardianEmail2;

	private ArrayList<Member.Guardian> _guardians;
	
	public GuardiansDialog(Context context, ArrayList<Member.Guardian> guardians, SetGuardiansHandler doneHandler) {
		_layoutInflater = LayoutInflater.from(context);
		_guardians = guardians;
		_doneHandler = doneHandler;
		initializeGuardiansView();
	}
	
	
	private void initializeGuardiansView() {
		_guardianView = _layoutInflater.inflate(R.layout.dlg_guardians, null);
		
		_txtGuardianFirstName1 = (EditText) _guardianView.findViewById(R.id.txtFirstName1);
		_txtGuardianLastName1 = (EditText) _guardianView.findViewById(R.id.txtLastName1);
		_txtGuardianEmail1 = (EditText) _guardianView.findViewById(R.id.txtEmail1);
		_txtGuardianFirstName2 = (EditText) _guardianView.findViewById(R.id.txtFirstName2);
		_txtGuardianLastName2 = (EditText) _guardianView.findViewById(R.id.txtLastName2);
		_txtGuardianEmail2 = (EditText) _guardianView.findViewById(R.id.txtEmail2);
		
		bindGuardiansView();
	}
	
	private void bindGuardiansView() {
		if (_guardians.size() > 0) {
			_txtGuardianFirstName1.setText(_guardians.get(0).firstName());
			_txtGuardianLastName1.setText(_guardians.get(0).lastName());
			_txtGuardianEmail1.setText(_guardians.get(0).emailAddress());
			
			if (_guardians.size() > 1) {
				_txtGuardianFirstName2.setText(_guardians.get(1).firstName());
				_txtGuardianLastName2.setText(_guardians.get(1).lastName());
				_txtGuardianEmail2.setText(_guardians.get(1).emailAddress());
			}
		}
	}
	
	public void showDialog() {		
		initializeGuardiansView();
		new AlertDialog.Builder(_layoutInflater.getContext())
			.setTitle("Specify Guardians for Member.")
			.setView(_guardianView)
			.setPositiveButton("Done", new OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { setGuardiansDone(); }})
			.show();
	}
	
	private void setGuardiansDone() {
		
		ArrayList<Member.Guardian> newGuardians = new ArrayList<Member.Guardian>();
		
		if (AndroidUtils.allHaveText(_txtGuardianFirstName1, _txtGuardianLastName1, _txtGuardianEmail1)) {
			newGuardians.add(getGuardian(_txtGuardianFirstName1, _txtGuardianLastName1, _txtGuardianEmail1, _guardians.size() > 0 ? _guardians.get(0) : null));
		}
		if (AndroidUtils.allHaveText(_txtGuardianFirstName2, _txtGuardianLastName2, _txtGuardianEmail2)) {
			newGuardians.add(getGuardian(_txtGuardianFirstName2, _txtGuardianLastName2, _txtGuardianEmail2, _guardians.size() > 1 ? _guardians.get(1) : null));
		}
		
		_guardians = newGuardians;
		
		if (_doneHandler != null) {
			_doneHandler.setGuardians(_guardians);
		}
	}
	
	private Member.Guardian getGuardian(EditText firstName, EditText lastName, EditText email, Member.Guardian origGuardian) {
		Member.Guardian guardian = new Member.Guardian(origGuardian);
		
		guardian.firstName(firstName.getText().toString());
		guardian.lastName(lastName.getText().toString());
		guardian.emailAddress(email.getText().toString());
		
		return guardian;
	}
}
