package com.rteam.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.rteam.android.common.AndroidTokenStorage;
import com.rteam.api.base.IUserTokenStorage;
import com.rteam.api.base.ResourceBase;
import com.rteam.api.base.ResourceResponse;
import com.rteam.api.base.APIResponse;
import com.rteam.api.business.User;
import com.rteam.api.business.UserCredentials;
import com.rteam.api.common.UriBuilder;

public class UsersResource extends ResourceBase {
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//// .ctor

	public UsersResource() {
		super(AndroidTokenStorage.get());
	}
		
	/////////////////////////////////////////////////////////////////////////////////////////////
	//// Response Classes
	
	public class CreateUserResponse extends ResourceResponse {
		
		private IUserTokenStorage _tokenStorage;
		
		private User _user;
		public User getUser() { return _user; }
		
		public CreateUserResponse(APIResponse response, IUserTokenStorage tokenStorage) {
			super(response);
			_tokenStorage = tokenStorage;
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood("UsersResource.CreateUserResponse")) {
				_user = new User(json());
				_tokenStorage.setUserToken(_user.token());
			}
		}
	}
	
	public interface CreateUserResponseHandler {
		public void finish(CreateUserResponse response);
	}
	
	public class UpdateUserResponse extends ResourceResponse {
		public UpdateUserResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	
	public interface UpdateUserResponseHandler {
		public void finish(UpdateUserResponse response);
	}
	
	public class UserAuthenticationResponse extends ResourceResponse {
		
		private User _user;
		public User getUser() { return _user; }
		
		private IUserTokenStorage _tokenStorage;

		public UserAuthenticationResponse(APIResponse response, IUserTokenStorage tokenStorage) {
			super(response);
			_tokenStorage = tokenStorage;
			
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood("UserResource")) {
				_user = new User(json());
				_tokenStorage.setUserToken(_user.token());
			}
		}
	}
	
	public interface UserAuthenticationResponseHandler {
		public void finish(UserAuthenticationResponse response);
	}
	
	public class GetPasswordResetResponse extends ResourceResponse {
		private String _passwordResetQuestion;
		public String getPasswordResetQuestion() { return _passwordResetQuestion; }
		
		public GetPasswordResetResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood("UserResource.PasswordResetResponse")) {
				try {
					if (json().has("passwordResetQuestion"))	_passwordResetQuestion = json().getString("passwordResetQuestion");
				} catch(JSONException e) { }
			}
		}
	}

	public interface GetPasswordResetResponseHandler {
		public void finish(GetPasswordResetResponse response);
	}
	
	public class PasswordResetResponse extends ResourceResponse {
		protected PasswordResetResponse(APIResponse response) {
			super(response);
			isResponseGood();
		}
	}
	public interface PasswordResetResponseHandler {
		public void finish(PasswordResetResponse response);
	}
	
	
	public class GetUserInfoResponse extends ResourceResponse {
		private UserCredentials _user;
		public UserCredentials getUser() { return _user; }
		
		protected GetUserInfoResponse(APIResponse response) {
			super(response);
			initialize();
		}
		
		private void initialize() {
			if (isResponseGood()) {
				_user = new UserCredentials(json());
			}
		}
	}

	public interface GetUserInfoResponseHandler {
		public void finish(GetUserInfoResponse response);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//// Exposed Methods
		
	public CreateUserResponse createUser(UserCredentials user) {
		return new CreateUserResponse(post(createBuilder(false).addPath("users"), user.toJSONCreate()), getTokenStorage());
	}
	
	public void createUser(final UserCredentials user, final CreateUserResponseHandler handler) {
		(new AsyncTask<Void, Void, CreateUserResponse>() {

			@Override
			protected CreateUserResponse doInBackground(Void... arg0) {
				return createUser(user);
			}
			
			@Override
			protected void onPostExecute(CreateUserResponse response) {
				handler.finish(response);
			}

		}).execute();
	}
	
	public UpdateUserResponse updateUser(UserCredentials user) {
		return new UpdateUserResponse(put(createBuilder().addPath("user"), user.toJSONUpdate()));
	}
	
	public void updateUser(final UserCredentials user, final UpdateUserResponseHandler handler) {
		(new AsyncTask<Void, Void, UpdateUserResponse>() {

			@Override
			protected UpdateUserResponse doInBackground(Void... arg0) {
				return updateUser(user);
			}
			
			@Override
			protected void onPostExecute(UpdateUserResponse response) {
				handler.finish(response);
			}

		}).execute();
	}

	public UserAuthenticationResponse getUserToken(UserCredentials user) {
		UriBuilder uri = createBuilder()
							.addPath("user")
							.addParam("emailAddress", user.emailAddress())
							.addParam("password", user.password());

		return new UserAuthenticationResponse(get(uri), getTokenStorage());
	}
	
	public void getUserToken(final UserCredentials user, final UserAuthenticationResponseHandler handler) {
		(new AsyncTask<Void, Void, UserAuthenticationResponse>() {

			@Override
			protected UserAuthenticationResponse doInBackground(Void... params) {
				return getUserToken(user);
			}
			
			@Override
			protected void onPostExecute(UserAuthenticationResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	
	public GetPasswordResetResponse getUserPasswordResetQuestion(String emailAddress) {
		UriBuilder uri = createBuilder().addPath("user").addPath("passwordResetQuestion")
							.addParam("emailAddress", emailAddress);		
		return new GetPasswordResetResponse(get(uri));
	}
	
	public void getUserPasswordResetQuestion(final String emailAddress, final GetPasswordResetResponseHandler handler) {
		(new AsyncTask<Void, Void, GetPasswordResetResponse>() {

			@Override
			protected GetPasswordResetResponse doInBackground(Void... params) {
				return getUserPasswordResetQuestion(emailAddress);
			}
			
			@Override
			protected void onPostExecute(GetPasswordResetResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	public PasswordResetResponse resetPassword(String emailAddress, String answer) {
		UriBuilder uri = createBuilder().addPath("user").addParam("emailAddress", emailAddress);
		JSONObject json = new JSONObject();
		try {
			json.putOpt("passwordResetAnswer", answer);
			json.putOpt("isPasswordReset", true);
		} catch (JSONException e) {}
		return new PasswordResetResponse(put(uri, json));
	}
	
	public void resetPassword(final String emailAddress, final String answer, final PasswordResetResponseHandler handler) {
		(new AsyncTask<Void, Void, PasswordResetResponse>() {

			@Override
			protected PasswordResetResponse doInBackground(Void... params) {
				return resetPassword(emailAddress, answer);
			}
			
			@Override
			protected void onPostExecute(PasswordResetResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
	
	
	public GetUserInfoResponse getUserInfo() {
		return new GetUserInfoResponse(get(createBuilder().addPath("user")));
	}
	
	public void getUserInfo(final GetUserInfoResponseHandler handler) {
		(new AsyncTask<Void, Void, GetUserInfoResponse>() {

			@Override
			protected GetUserInfoResponse doInBackground(Void... params) {
				return getUserInfo();
			}
			
			@Override
			protected void onPostExecute(GetUserInfoResponse response) {
				handler.finish(response);
			}
			
		}).execute();
	}
}
