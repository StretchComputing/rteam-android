package com.rteam.api.business;

import org.json.JSONObject;

import com.rteam.api.common.JSONUtils;

public class UserCredentials {
	
	private String _emailAddress;
	public String emailAddress() { return _emailAddress; }
	public void emailAddress(String value) { _emailAddress = value; }
	
	private String _password;
	public String password() { return _password; }
	public void password(String value) { _password = value; }
	
	private String _firstName;
	public String firstName() { return _firstName; }
	public void firstName(String value) { _firstName = value; }
	
	private String _lastName;
	public String lastName() { return _lastName; }
	public void lastName(String value) { _lastName = value; }
	
	private String _passwordResetQuestion;
	public String passwordResetQuestion() { return _passwordResetQuestion; }
	public void passwordResetQuestion(String value) { _passwordResetQuestion = value; }
	
	private String _passwordResetAnswer;
	public String passwordResetAnswer() { return _passwordResetAnswer; }
	public void passwordResetAnswer(String value) { _passwordResetAnswer = value; }
	
	private String _phoneNumber;
	public String phoneNumber() { return _phoneNumber; }
	public void phoneNumber(String value) { _phoneNumber = value; }
	
	
	
	
	public UserCredentials() {}
	
	public UserCredentials(String emailAddress, String password) {
		emailAddress(emailAddress);
		password(password);
	}
	
	public UserCredentials(String emailAddress, String password, String firstName, String lastName) {
		this(emailAddress, password);
		firstName(firstName);
		lastName(lastName);
	}
	
	public UserCredentials(JSONObject json) {
		firstName(json.optString("firstName"));
		lastName(json.optString("lastName"));
		emailAddress(json.optString("emailAddress"));
		phoneNumber(json.optString("phoneNumber"));
		passwordResetQuestion(json.optString("passwordResetQuestion"));
	}
	
	public JSONObject toJSONCreate() {
		JSONObject params = new JSONObject();
		
		JSONUtils.put(params, "firstName", firstName()); 
		JSONUtils.put(params, "lastName", lastName());
		JSONUtils.put(params, "emailAddress", emailAddress());
		JSONUtils.put(params, "password", password());
				
		return params;
	}
	
	public JSONObject toJSONUpdate() {
		JSONObject params = new JSONObject();
		
		JSONUtils.put(params, "emailAddress", emailAddress());
		JSONUtils.put(params, "password", password());
		JSONUtils.put(params, "passwordResetQuestion", passwordResetQuestion());
		JSONUtils.put(params, "passwordResetAnswer", passwordResetAnswer());
		JSONUtils.put(params, "phoneNumber", phoneNumber());
		
		return params;
	}
}
