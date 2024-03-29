package com.rteam.android.user;

import com.rteam.android.R;
import com.rteam.android.common.RTeamAnalytics;
import com.rteam.api.UsersResource;
import com.rteam.api.UsersResource.GetPasswordResetResponse;
import com.rteam.api.UsersResource.PasswordResetResponse;
import com.rteam.api.base.ResponseStatus;
import com.rteam.api.common.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPasswordDialog {
	
	private Activity _context;
	private RTeamAnalytics _tracker;
	
	private View _view;
	
	private TextView _lblLabel;
	private EditText _txtEmail;
	private EditText _txtAnswer;
	private Button _btnCheckEmail;
	private ProgressBar _loadingProgress;
	
	private AlertDialog _dlg;
	private Button _btnResetPassword;
	
	
	public ResetPasswordDialog(Activity context, RTeamAnalytics tracker) {
		_context = context;
		_tracker = tracker;
		initializeView();
	}
	
	private void initializeView() {
		_view = LayoutInflater.from(_context).inflate(R.layout.user_resetpassword, null);
		
		_lblLabel = (TextView) _view.findViewById(R.id.lblLabel);
		_txtEmail = (EditText) _view.findViewById(R.id.txtEmail);
		_txtAnswer = (EditText) _view.findViewById(R.id.txtAnswer);
		_btnCheckEmail = (Button) _view.findViewById(R.id.btnCheckEmail);
		_loadingProgress = (ProgressBar) _view.findViewById(R.id.loadingProgress);
		
		_btnCheckEmail.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { checkEmail(); }
		});
	}
	
	
	public void showDialog() {
		_dlg = new AlertDialog.Builder(_context)
					.setTitle("Reset Password")
					.setView(_view)
					.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { reset(); } })
					.setNegativeButton("Cancel", null)
					.show();
		_btnResetPassword = (Button) _dlg.getButton(AlertDialog.BUTTON_POSITIVE);
		_btnResetPassword.setEnabled(false);
	}
	
	private void checkEmail() {
		_loadingProgress.setVisibility(View.VISIBLE);
		UsersResource.instance().getUserPasswordResetQuestion(_txtEmail.getText().toString(), new UsersResource.GetPasswordResetResponseHandler() {
			@Override public void finish(GetPasswordResetResponse response) { checkEmailFinished(response); }
		});
	}
	
	private void checkEmailFinished(GetPasswordResetResponse response) {
		if(response.showError(_context)) {
			if (StringUtils.isNullOrEmpty(response.getPasswordResetQuestion())) {
				reset();
			}
			else {
				_btnCheckEmail.setVisibility(View.GONE);
				_loadingProgress.setVisibility(View.GONE);
				_txtEmail.setVisibility(View.GONE);
				_lblLabel.setText(response.getPasswordResetQuestion());
				_txtAnswer.setVisibility(View.VISIBLE);
				_btnResetPassword.setEnabled(true);
			}
		}
		else {
			_loadingProgress.setVisibility(View.GONE);
		}
	}
	
	private void reset() {
		UsersResource.instance().resetPassword(_txtEmail.getText().toString(), _txtAnswer.getText().toString(), new UsersResource.PasswordResetResponseHandler() {
			@Override public void finish(PasswordResetResponse response) { tryResetFinished(response); }
		});
	}
	
	private void tryResetFinished(PasswordResetResponse response) {
		_tracker.trackResetPassword(response.getStatus());
		if (response.getStatus() == ResponseStatus.PasswordResetFailed) {
			Toast.makeText(_context, "Invalid answer to reset question, please try again.", Toast.LENGTH_SHORT).show();
		}
		else {
			finishReset();
		}
	}
	
	
	private void finishReset() {
		Toast.makeText(_context, "Success! You will recieve your new password via email", Toast.LENGTH_SHORT).show();
		_dlg.dismiss();
	}

}
