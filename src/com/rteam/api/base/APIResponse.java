package com.rteam.api.base;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.android.common.RTeamLog;

public class APIResponse {
		
	/* Members */
	private ResponseStatus _status;
	private HttpResponse _response;
	private JSONObject _jsonResponse;
	private String _stringResponse;
	private final RTeamLog.TagSuffix LOG_SUFFIX = new RTeamLog.TagSuffix("api.response");
			
	/* .ctor */
	public APIResponse(HttpResponse response) {
		_status = ResponseStatus.Unknown;
		
		if (response == null) {
			_status = ResponseStatus.NoResponse;
			RTeamLog.w(LOG_SUFFIX, "HTTP Response is null");
			return;
		}
		
		_response = response;
		_stringResponse = "";
		_jsonResponse = new JSONObject();
				
		if (response.getEntity() != null) {
			try {
				RTeamLog.d(LOG_SUFFIX, "HTTP Response is not null");
				_stringResponse = EntityUtils.toString(response.getEntity());
				RTeamLog.d(LOG_SUFFIX, "HTTP String Response: '%s'.", _stringResponse);
			} catch (IllegalStateException e) {} catch (IOException e) {}	
		}
		
		RTeamLog.d(LOG_SUFFIX, "Attempting to parse JSON.");
		if (_stringResponse.length() != 0) {
			try {
				_jsonResponse = new JSONObject(_stringResponse);
				RTeamLog.d(LOG_SUFFIX, "JSON Parsed: '%s'.", _jsonResponse.toString());
				
				if (_jsonResponse.has("apiStatus")) {
					_status = ResponseStatus.valueOfCode(_jsonResponse.getString("apiStatus"));
				}
			} catch (JSONException e) {}
		}
	}

	/* Accessors */
	public ResponseStatus getStatus() { return _status; }
	public HttpResponse getResponse() { return _response; }
	public JSONObject getJSONResponse() { return _jsonResponse; }
	public String getStringResponse() { return _stringResponse; }
	
	/* Debugging */
	protected boolean isResponseGood() {
		if (getJSONResponse() == null) {
			RTeamLog.w(LOG_SUFFIX, "APIResponse: JSON Response is null.");
			return false;
		}
		
		if (getStatus() != ResponseStatus.Success) {
			RTeamLog.w(LOG_SUFFIX, "APIResponse: Status is not success: %d(%s).", getStatus().getCode(), getStatus().toString());
			return false;
		}
		
		return true;
	}
}
