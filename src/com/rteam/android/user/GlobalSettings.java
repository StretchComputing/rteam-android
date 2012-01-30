package com.rteam.android.user;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rteam.android.Home;
import com.rteam.android.R;
import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.HelpProvider;
import com.rteam.android.common.RTeamActivityChildTab;
import com.rteam.android.common.RTeamLog;
import com.rteam.android.common.SimpleSetting;
import com.rteam.android.teams.common.TeamCache;
import com.rteam.api.UsersResource;
import com.rteam.api.UsersResource.UpdateUserResponse;
import com.rteam.api.business.UserCredentials;
import com.rteam.api.common.StringUtils;


public class GlobalSettings extends RTeamActivityChildTab 
implements ChangePasswordDialog.ChangePasswordHandler, SetResetPasswordDialog.SaveResetPasswordHandler {
	
	private Button _btnChangePassword;
	private Button _btnPasswordReset;
	private ToggleButton _btnAutoLogin;
	private ToggleButton _btnShowAlerts;
	private Button _btnLogout;
	
	@Override
	protected String getCustomTitle() { return "rTeam - global settings"; }
	
	@Override
	protected HelpProvider getHelpProvider() {
		return new HelpProvider(new HelpProvider.HelpContent("Change Password", "Change the password of the current user."),
							    new HelpProvider.HelpContent("Set Password Reset Question", "Setup or alter a password reset question and answer for you to answer if you forget your password."),
							    new HelpProvider.HelpContent("Auto Login", "Whether or not to remember the current user when opening the application in the future."),
							    new HelpProvider.HelpContent("Show Alerts", "Whether or not to show alerts when a game/practice is upcoming for one of your teams."),
							    new HelpProvider.HelpContent("Logout", "Logs the current user out."));		 
	}
	
	@Override
	protected void initialize()
	{
		initializeView();
	}
	
	private void initializeView()
	{
		setContentView(R.layout.user_settings);
		
		_btnChangePassword = (Button)findViewById(R.id.btnChangePassword);
		_btnPasswordReset = (Button)findViewById(R.id.btnPasswordReset);
		_btnAutoLogin = (ToggleButton)findViewById(R.id.btnAutoLogin);
		_btnShowAlerts = (ToggleButton)findViewById(R.id.btnShowAlerts);
		_btnLogout = (Button)findViewById(R.id.btnLogout);
		
		_btnAutoLogin.setChecked(SimpleSetting.AutoLogin.getBoolean());
		_btnShowAlerts.setChecked(SimpleSetting.ShowAlerts.getBoolean());
		
		_btnChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { changePassword(); }
		});
		
		_btnPasswordReset.setOnClickListener(new View.OnClickListener() {		
			@Override public void onClick(View v) { setPasswordReset(); }
		});
		
		_btnAutoLogin.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setAutoLogin(_btnAutoLogin.isChecked()); }
		});
		
		_btnShowAlerts.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setAlerts(_btnShowAlerts.isChecked()); }
		});
		
		_btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { logout(); }
		});
	}
			
	private void logout() {
		TeamCache.clear();
		AndroidTokenStorage.get().setUserToken(null);
		moveTaskToBack(true);
		Intent home = new Intent(this, Home.class);
		home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		startActivity(home);
	}
	
	private void setAutoLogin(boolean autoLogin) {
		RTeamLog.i("Setting Auto Login: %s", Boolean.toString(autoLogin));
		SimpleSetting.AutoLogin.set(autoLogin);
		
		// Reset the token storage
		AndroidTokenStorage storage = AndroidTokenStorage.get();
		if(!autoLogin) {
			storage.clear();
		}
		else {
			storage.setUserToken(storage.getUserToken());
		}
	}
	
	private void setAlerts(boolean alerts) {
		RTeamLog.i("Setting Show Alerts: %s", Boolean.toString(alerts));
		SimpleSetting.ShowAlerts.set(alerts);
	}
	
	private void changePassword() {
		if (isFinishing()) return;
		
		new ChangePasswordDialog(this, this).showDialog();
	}
	@Override
	public void changePassword(String newPassword) {
		if (StringUtils.isNullOrEmpty(newPassword)) return;
		
		UserCredentials user = getUser();
		user.password(newPassword);
		user.passwordResetQuestion(null);
		CustomTitle.setLoading(true, "Saving...");
		UsersResource.instance().updateUser(user, new UsersResource.UpdateUserResponseHandler() {
			@Override public void finish(UpdateUserResponse response) { saveSuccess(response); }
		});			
	}
	
	
	
	
	private void setPasswordReset() {
		if (isFinishing()) return;
		
		new SetResetPasswordDialog(this, this).showDialog();
	}
	@Override
	public void saveResetPassword(String question, String answer) {
		if (StringUtils.isNullOrEmpty(question) || StringUtils.isNullOrEmpty(answer)) return;
		
		UserCredentials user = getUser();
		user.passwordResetQuestion(question);
		user.passwordResetAnswer(answer);
		CustomTitle.setLoading(true, "Saving...");
		UsersResource.instance().updateUser(user, new UsersResource.UpdateUserResponseHandler() {
			@Override public void finish(UpdateUserResponse response) { saveSuccess(response); }
		});
	}
	

	
	private void saveSuccess(UpdateUserResponse response) {
		CustomTitle.setLoading(false);
		if (response.showError(this)) {
			Toast.makeText(this, "Saved Successfully...", Toast.LENGTH_SHORT).show();
		}
	}

	
	private UserCredentials getUser() { return UsersResource.instance().getUserInfo().getUser(); }
}
