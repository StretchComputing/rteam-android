package com.rteam.android.user;

import com.rteam.android.R;
import com.rteam.api.common.StringUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordDialog {
	
	public interface ChangePasswordHandler {
		public void changePassword(String newPassword);
	}
	
	private ChangePasswordHandler _changePasswordHandler;
	
	private Context _context;
	
	private View _view;
	
	private EditText _txtPassword;
	private EditText _txtPasswordConfirm;
	
	private AlertDialog _dlg;
	private Button _saveButton;
	
	public ChangePasswordDialog(Context context, ChangePasswordHandler handler) {
		_context = context;
		_changePasswordHandler = handler;
		initializeView();
	}
	
	
	private void initializeView() {
		_view = LayoutInflater.from(_context).inflate(R.layout.user_changepassword, null);
		
		_txtPassword = (EditText) _view.findViewById(R.id.txtPassword);
		_txtPasswordConfirm = (EditText) _view.findViewById(R.id.txtPasswordConfirm);
		
		_txtPassword.setOnKeyListener(new View.OnKeyListener() { 
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { validatePassword(); return false; }
		});
		_txtPasswordConfirm.setOnKeyListener(new View.OnKeyListener() { 
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { validatePassword(); return false; }
		});
	}
	
	public void showDialog() {
		_dlg = new AlertDialog.Builder(_context)
					.setTitle("Change Password")
					.setView(_view)
					.setPositiveButton("Save", new DialogInterface.OnClickListener() { 
						@Override public void onClick(DialogInterface dialog, int which) { savePassword(); } })
					.setNegativeButton("Cancel", null)
					.show();
		_saveButton = _dlg.getButton(AlertDialog.BUTTON_POSITIVE);
		_saveButton.setEnabled(false);
	}
	
	private void validatePassword() {
		_saveButton.setEnabled(!StringUtils.isNullOrEmpty(_txtPassword.getText().toString())
								&& !StringUtils.isNullOrEmpty(_txtPasswordConfirm.getText().toString())
			 					&& _txtPassword.getText().toString().equals(_txtPasswordConfirm.getText().toString()));
	}

	private void savePassword() {
		if (_changePasswordHandler != null) {
			_changePasswordHandler.changePassword(_txtPassword.getText().toString());
		}
	}
}
