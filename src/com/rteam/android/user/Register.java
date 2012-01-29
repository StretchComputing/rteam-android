package com.rteam.android.user;

import com.rteam.android.R;
import com.rteam.android.Home;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.RTeamActivity;
import com.rteam.android.common.SimpleSetting;
import com.rteam.api.UsersResource;
import com.rteam.api.UsersResource.CreateUserResponse;
import com.rteam.api.UsersResource.UserAuthenticationResponse;
import com.rteam.api.base.ResponseStatus;
import com.rteam.api.business.UserCredentials;
import com.rteam.api.common.StringUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends RTeamActivity {

	/////////////////////////////////////////////////////////////////////
	//// Members
	
	private EditText _txtEmailAddress;
	private EditText _txtPassword;
	private EditText _txtFirstName;
	private EditText _txtLastName;
	
	private TextView _lblExistingMember;
	private Button _btnCreateAccount;
	
	private TextView _lblOutput;
	
	private View _loginView;
	
	private EditText _loginTxtEmail;
	private EditText _loginTxtPassword;
	
	private TextView _lblForgotPassword;
	
	private AlertDialog _loginDialog;
	
	@Override protected String getCustomTitle() { return "rTeam - register"; }
	@Override protected boolean showMenu() { return false; }
	
	/////////////////////////////////////////////////////////////////////
	//// Accessors
	
	private String getEmailAddress() { return _txtEmailAddress.getText().toString(); }
	private Boolean hasEmailAddress() { return getEmailAddress() != null && getEmailAddress().length() > 0; }
	private String getPassword() { return _txtPassword.getText().toString(); }
	private Boolean hasPassword() { return getPassword() != null && getPassword().length() > 0; }
	private String getFirstName() { return _txtFirstName.getText().toString(); }
	private Boolean hasFirstName() { return getFirstName() != null && getFirstName().length() > 0; }
	private String getLastName() { return _txtLastName.getText().toString(); }
	private Boolean hasLastName() { return getLastName() != null && getLastName().length() > 0; }
	
	private Boolean hasAllFields() { return hasEmailAddress() && hasPassword() && hasFirstName() && hasLastName(); }
	
	private void setOutput(String message) { 
		_lblOutput.setText(message);
		_lblOutput.setVisibility(StringUtils.isNullOrEmpty(message) ? View.GONE : View.VISIBLE);
	}
	
    @Override
    public boolean isSecure() { return false; }
	
    
    /////////////////////////////////////////////////////////////////////
	//// Initialization
	
    @Override
    protected void initialize() {
    	initializeView();
    	setOutput("");
    }
    
    private void initializeView() {
    	setContentView(R.layout.register);
    	
    	_txtEmailAddress = (EditText) findViewById(R.id.txtEmailAddress);
    	_txtPassword = (EditText) findViewById(R.id.txtPassword);
    	_txtFirstName = (EditText) findViewById(R.id.txtFirstName);
    	_txtLastName = (EditText) findViewById(R.id.txtLastName);
    	
    	_lblOutput = (TextView) findViewById(R.id.lblOutput);
    	
    	_lblExistingMember = (TextView) findViewById(R.id.lblExistingMember);
    	_btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
    	
    	_lblExistingMember.setOnClickListener(new View.OnClickListener() {
    		@Override public void onClick(View v) { loginExistingMember(); }
    	});
    	
    	_btnCreateAccount.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { onCreateAccountClick(); }
		});
    	
    	_txtEmailAddress.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
    	_txtPassword.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
    	_txtFirstName.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
    	_txtLastName.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
    }
    
    private void initializeLoginView() {
    	_loginView = (View) getLayoutInflater().inflate(R.layout.dlg_login, null);
    	
    	_loginTxtEmail = (EditText) _loginView.findViewById(R.id.txtEmail);
    	_loginTxtPassword = (EditText) _loginView.findViewById(R.id.txtPassword);
    	_lblForgotPassword = (TextView) _loginView.findViewById(R.id.lblForgotPassword);
    	
    	_lblForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { onForgotPasswordClick(); }			
		});
    }
    
    private void bindButtons() {
    	_btnCreateAccount.setEnabled(StringUtils.hasText(_txtEmailAddress)
    								  && StringUtils.hasText(_txtPassword)
    								  && StringUtils.hasText(_txtFirstName)
    								  && StringUtils.hasText(_txtLastName));			
    }
    
    
    /////////////////////////////////////////////////////////////////////
	//// Event Handlers
    
    private UserCredentials getUserCredentials() {
    	return new UserCredentials(getEmailAddress(), getPassword(), getFirstName(), getLastName());
    }
    
    private void onCreateAccountClick() {
    	if (isFinishing()) return;
    	
		if (!hasAllFields()) {
			setOutput("All fields are required to register.");
			return;
		}
		
		CustomTitle.setLoading(true, "Registering...");
		UsersResource.instance().createUser(getUserCredentials(), new UsersResource.CreateUserResponseHandler() {
			@Override public void finish(CreateUserResponse response) {
				CustomTitle.setLoading(false);
				if (response.getStatus() == ResponseStatus.EmailAddressAlreadyUsed) {
					setOutput("The email address provided is already in use.");
					return;
				}
				else if (response.getStatus() != ResponseStatus.Success) {
					setOutput(String.format("Unknown error: %s", response.getStatus()));
					return;
				}
				setOutput("");
				finishRegistering();
			}
		});
    }
    
    private void finishRegistering() {
    	// Make sure the db is set that the user hasn't seen the wizard in the off chance they re-create an account
    	// on the same device.
    	SimpleSetting.SeenWizard.set(false);
    	finish();
		startActivity(new Intent(Register.this, Home.class));
    }
    
    private void onForgotPasswordClick() {
    	if (isFinishing()) {
    		return;
    	}
    	
    	_loginDialog.hide();
    	new ResetPasswordDialog(this).showDialog();
    }
    
    private void loginExistingMember() {
    	if (isFinishing()) return;
    	
    	initializeLoginView();
    	
    	_loginDialog = new AlertDialog.Builder(this)
    			.setView(_loginView)
    			.setTitle("Login with existing Email")
    			.setPositiveButton("Login", new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { 
						attemptLogin(); 
					}})
				.setNegativeButton("Cancel", null).create();
    	
    	_loginDialog.show();
    }
    
    private void attemptLogin() {
    	CustomTitle.setLoading(true, "Attempting to login");
    	UsersResource.instance().getUserToken(new UserCredentials(_loginTxtEmail.getText().toString(), _loginTxtPassword.getText().toString()), new UsersResource.UserAuthenticationResponseHandler() {
			@Override public void finish(UserAuthenticationResponse response) {
				CustomTitle.setLoading(false);
				if (response.getStatus() == ResponseStatus.InvalidUserCredentials) {
					loginExistingMember();
					Toast.makeText(Register.this, "Invalid user credentials entered, please try again.", Toast.LENGTH_SHORT).show();
				}
				else if (response.getStatus() != ResponseStatus.Success) {
					Toast.makeText(Register.this, String.format("Unknown response: %s", response.getStatus().toString()), Toast.LENGTH_SHORT).show();
				}
				else {
					attemptLoginFinish();
				}
			}
    	});
    }
    
    private void attemptLoginFinish() {
    	finish();
    	startActivity(new Intent(this, Home.class));
    }
    
    
    /////////////////////////////////////////////////////////////////////
	//// Menu
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register, menu);
        return true;
    }
}
