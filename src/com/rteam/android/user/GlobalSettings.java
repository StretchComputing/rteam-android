package com.rteam.android.user;

import android.content.Intent;
import android.widget.Toast;

import com.rteam.android.Home;
import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.SimpleExpandableListActivity;
import com.rteam.android.common.SimpleExpandableListClickListener;
import com.rteam.android.common.SimpleListItem;
import com.rteam.android.common.SimpleSetting;
import com.rteam.api.UsersResource;
import com.rteam.api.UsersResource.UpdateUserResponse;
import com.rteam.api.business.UserCredentials;
import com.rteam.api.common.StringUtils;


public class GlobalSettings extends SimpleExpandableListActivity 
implements ChangePasswordDialog.ChangePasswordHandler, SetResetPasswordDialog.SaveResetPasswordHandler {
	
	@Override
	protected void loadListItems() {
		addItem("Password : ", "Change Password", new SimpleExpandableListClickListener() {
			@Override public void onClick(SimpleListItem item) { changePassword(); }
		});
		addItem("Password : ", "Set Password Reset Question", new SimpleExpandableListClickListener() {
			@Override public void onClick(SimpleListItem item) { setPasswordReset(); }
		});
		
		addCheckItem("Other : ", "Auto Login", true, new SimpleExpandableListClickListener() {
			public void onClick(SimpleListItem item) { setAutoLogin(item.isChecked()); }
		});
		addCheckItem("Other : ", "Alerts", true, new SimpleExpandableListClickListener() {
			public void onClick(SimpleListItem item) { setAlerts(item.isChecked()); }
		});
		addItem("Other : ", "Log Out", new SimpleExpandableListClickListener() {
			public void onClick(SimpleListItem item) { logout(); }
		});
	}
	
	@Override
	protected void afterInitialize() {
		CustomTitle.setTitle("rTeam - settings");
		
		for (int i=0; i<getExpandableListAdapter().getGroupCount(); i++) {
			getExpandableListView().expandGroup(i);
		}
	}
			
	private void logout() {
		AndroidTokenStorage.get().setUserToken(null);
		moveTaskToBack(true);
		Intent home = new Intent(this, Home.class);
		home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		startActivity(home);
	}
	
	private void setAutoLogin(boolean autoLogin) {
		SimpleSetting.AutoLogin.set(autoLogin);
		if (AndroidTokenStorage.get() != null) {
			AndroidTokenStorage.get().clear();
		}
	}
	
	private void setAlerts(boolean alerts) {
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
