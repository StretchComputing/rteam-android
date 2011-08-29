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

public class SetResetPasswordDialog {
	
	public interface SaveResetPasswordHandler {
		public void saveResetPassword(String question, String answer);
	}
	
	private SaveResetPasswordHandler _resetPasswordHandler;
	
	private Context _context;
	
	private View _view;
	
	private EditText _txtQuestion;
	private EditText _txtAnswer;
	
	private AlertDialog _dlg;
	private Button _saveButton;
	
	public SetResetPasswordDialog(Context context, SaveResetPasswordHandler handler) {
		_context = context;
		_resetPasswordHandler = handler;
		initializeView();
	}
	
	private void initializeView() {
		_view = LayoutInflater.from(_context).inflate(R.layout.user_setreset, null);
		_txtQuestion = (EditText) _view.findViewById(R.id.txtQuestion);
		_txtAnswer = (EditText) _view.findViewById(R.id.txtAnswer);
		
		_txtQuestion.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { validate(); return false; }
		});
		_txtAnswer.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { validate(); return false; }
		});
	}
	
	
	public void showDialog() {
		_dlg = new AlertDialog.Builder(_context)
					.setTitle("Set Reset Password")
					.setView(_view)
					.setPositiveButton("Save", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { savePasswortReset(); } })
					.setNegativeButton("Cancel", null)
					.show();
		_saveButton = _dlg.getButton(AlertDialog.BUTTON_POSITIVE);
		_saveButton.setEnabled(false);
	}
	
	private void validate() {
		_saveButton.setEnabled(!StringUtils.isNullOrEmpty(_txtQuestion.getText().toString())
								&& !StringUtils.isNullOrEmpty(_txtAnswer.getText().toString()));
	}
	
	private void savePasswortReset() {
		if (_resetPasswordHandler != null) {
			_resetPasswordHandler.saveResetPassword(_txtQuestion.getText().toString(), _txtAnswer.getText().toString());
		}
	}

}
