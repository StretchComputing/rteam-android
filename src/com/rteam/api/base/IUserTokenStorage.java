package com.rteam.api.base;

public interface IUserTokenStorage {
	
	public String getUserToken();
	
	public void setUserToken(String token);
	
	public boolean hasUserToken();
}
