package com.rteam.api.business;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	private String _emailAddress;
	public String emailAddress() { return _emailAddress; }
	public void emailAddress(String value) { _emailAddress = value; }
	
	private String _firstName;
	public String firstName() { return _firstName; }
	public void firstName(String value) { _firstName = value; }
	
	private String _lastName;
	public String lastName() { return _lastName; }
	public void lastName(String value) { _lastName = value; }
		
	private String _token;
	public String token() { return _token; } 
	public void token(String value) { _token = value; }
	
	private String _userIconOneId;
	public String userIconOneId() { return _userIconOneId; } 
	public void userIconOneId(String value) { _userIconOneId = value; }
	
	private String _userIconOneAlias;
	public String userIconOneAlias() { return _userIconOneAlias; }
	public void userIconOneAlias(String value) { _userIconOneAlias = value; }
	
	private String _userIconTwoId;
	public String userIconTwoId() { return _userIconTwoId; }
	public void userIconTwoId(String value) { _userIconTwoId = value; }
	
	private String _userIconTwoAlias;
	public String userIconTwoAlias() { return _userIconTwoAlias; }
	public void userIconTwoAlias(String value) { _userIconTwoAlias = value; }
	
	public User(JSONObject json) {
		try {
			if (json.has("token")) 				token(json.getString("token"));
			if (json.has("userIconOneId")) 		userIconOneId(json.getString("userIconOneId"));
			if (json.has("userIconOneAlias"))	userIconOneAlias(json.getString("userIconOneAlias"));
			if (json.has("userIconTwoId"))		userIconTwoId(json.getString("userIconTwoId"));
			if (json.has("userIconTwoAlias"))	userIconTwoAlias(json.getString("userIconTwoAlias"));
		} catch(JSONException e) {}
	}
	
}
