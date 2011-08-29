package com.rteam.android.common;

import android.content.Context;

import com.rteam.api.base.IUserTokenStorage;

public class AndroidTokenStorage implements IUserTokenStorage {

	private static final String TOKEN_NAME = "UserToken";
	private static String _currentToken = null;
	
	private SimpleSettings _dataStore;
	
	public AndroidTokenStorage(SimpleSettings dataStore) {
		_dataStore = dataStore;
	}
	
	private static AndroidTokenStorage _tokenStorage = null;
	public static void initialize(Context context) {
		if (_tokenStorage == null) {
			SimpleSettings.initialize(context);
			_tokenStorage = new AndroidTokenStorage(SimpleSettings.get());
		}
	}
	public static AndroidTokenStorage get() { return _tokenStorage; }
	
	@Override
	public String getUserToken() {
		if (_currentToken == null) {
			setUserToken(_dataStore.get(TOKEN_NAME));
		}
		return _currentToken;
	}

	@Override
	public void setUserToken(String token) {
		_currentToken = token;
		RTeamLog.i("Setting User Token: %s", token);
		if (SimpleSetting.AutoLogin.getBoolean(true)) {
			RTeamLog.i("Setting in data store.");
			_dataStore.set(TOKEN_NAME, token);
		}
	}
	
	public void clear() {
		_dataStore.set(TOKEN_NAME, (String)null);
	}
	
	@Override
	public boolean hasUserToken() {
		String userToken = getUserToken();
		return userToken != null && userToken.length() > 0;
	}
}
