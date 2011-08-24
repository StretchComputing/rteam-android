package com.rteam.api.base;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.rteam.android.common.RTeamLog;

public class ResourceResponse {
	private final RTeamLog.TagSuffix LOG_SUFFIX = new RTeamLog.TagSuffix("api.resource");
	private APIResponse _response;
	
	public APIResponse response() { return _response; }
	public ResponseStatus getStatus() { return _response != null ? _response.getStatus() : ResponseStatus.NoResponse; }
	protected JSONObject json() { return _response != null ? _response.getJSONResponse() : new JSONObject(); }
	
	protected ResourceResponse(APIResponse response) {
		_response = response;
	}
	
	
	/* Basic Response Checking */
	protected boolean isResponseGood() { return isResponseGood(this.getClass().getSimpleName()); }
	protected boolean isResponseGood(String identifier) {
		if (response() == null) {
			RTeamLog.w(LOG_SUFFIX, "%s: Response is null.", identifier);
			return false;
		}
		
		if (json() == null) {
			RTeamLog.w(LOG_SUFFIX, "%s: Response JSON is null.", identifier);
			return false;
		}
		else {
			RTeamLog.d(LOG_SUFFIX, "%s: JSON = '%s'.", identifier, json().toString());
		}
		
		if (getStatus() != ResponseStatus.Success) {
			RTeamLog.w(LOG_SUFFIX, "%s: Response status is not success : %d(%s)", identifier, getStatus().getCode(), getStatus().toString()); 
			return false;
		}

		return true;
	}	
	
	/* Helper for checking response */
	public boolean checkResponse() {
		return getStatus() == ResponseStatus.Success;
	}
	
	public boolean showError(Context context) {
		if (checkResponse()) {
			return true;
		}
		
		if (getStatus().hasErrorMessage()) {
			Toast.makeText(context, getStatus().getErrorMessage(), Toast.LENGTH_SHORT).show();
		}
		
		return false;
	}
}
