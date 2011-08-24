package com.rteam.api.base;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.rteam.android.common.RTeamLog;
import com.rteam.api.common.Base64Utils;
import com.rteam.api.common.UriBuilder;

public abstract class ResourceBase {
	/* Members */
	private String BASE_URL;
	private String SECURE_BASE_URL;
	
	private IUserTokenStorage _tokenStorage;
	private DefaultHttpClient _client;
	
	private final RTeamLog.TagSuffix LOG_SUFFIX = new RTeamLog.TagSuffix("api");
	
	/* .ctor */	
	protected ResourceBase(IUserTokenStorage tokenStorage) {
		this(tokenStorage, "http://rteamtest.appspot.com/", "https://rteamtest.appspot.com/");
	}
	protected ResourceBase(IUserTokenStorage tokenStorage, String baseUrl, String secureBaseUrl) {
		this(tokenStorage, baseUrl, secureBaseUrl, new DefaultHttpClient());
	}
	
	private ResourceBase(IUserTokenStorage tokenStorage, String baseUrl, String secureBaseUrl, DefaultHttpClient client) {
		_tokenStorage = tokenStorage;
		BASE_URL = baseUrl;
		SECURE_BASE_URL = secureBaseUrl;
		_client = client;
	}
	
	/* Overridable Functions */
	protected String getDebugTag() {
		return "rteam.resource";
	}
	
	protected UriBuilder createBuilder() { return createBuilder(true); }
	protected UriBuilder createBuilder(boolean useSecure) {
		return new UriBuilder(useSecure ? SECURE_BASE_URL : BASE_URL);
	}
	
	
	/* Put Functions */
	protected APIResponse put(UriBuilder uri, JSONObject data) { return put(uri.toString(), toEntity(data)); }
	private APIResponse put(String url, HttpEntity data) {
		return makeRequest(new HttpPut(url), data);
	}
	
	
	/* Post Functions */
	protected APIResponse post(UriBuilder uri, JSONObject data) { return post(uri.toString(), toEntity(data)); }
	private APIResponse post(String url, HttpEntity data) {
		return makeRequest(new HttpPost(url), data);
	}
	
	/* Delete Functions */
	protected APIResponse delete(UriBuilder uri) { return delete(uri.toString()); }
	private APIResponse delete(String url) {
		return makeRequest(new HttpDelete(url));
	}
	
	/* Get */
	protected APIResponse get(UriBuilder uri) { return get(uri.toString()); }
	private APIResponse get(String url) {
		return makeRequest(new HttpGet(url));
	}
	
	/* Encoding */
	protected String getUrlEncoding() { return "UTF-8"; }
	protected String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, getUrlEncoding());
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}
	
	
	/* Generic Requests */
	private APIResponse makeRequest(HttpEntityEnclosingRequestBase request, HttpEntity data) {
		if (data != null) request.setEntity(data);
		return makeRequest(request);
	}
	private APIResponse makeRequest(HttpUriRequest request) {
		addExtraHeaders(request);
						
		try {
			logInfo("Attempting to %s from '%s' %s", request.getMethod(), request.getURI(), (request.getHeaders("Authorization").length == 1 ? "with auth" : "without auth"));
			if (request instanceof HttpEntityEnclosingRequestBase) {
				logInfo("Entity: %s", getStringFromEntity(((HttpEntityEnclosingRequestBase)request).getEntity()));
			}
			logInfo("Headers: ");
			for(Header header : request.getAllHeaders()) {
				logInfo(header.toString());
			}
						
			return new APIResponse(_client.execute(request));
		} catch (ClientProtocolException e) {
			logError(request.getMethod(), request.getURI().toString(), e);
		} catch (IOException e) {
			logError(request.getMethod(), request.getURI().toString(), e);
		}
		
		logInfo("Error occurred while making request.");
		return null;
	}
	
	private String getStringFromEntity(HttpEntity entity) {
		try {
			return EntityUtils.toString(entity);
		}
		catch (ParseException e) { } 
		catch (IOException e) { }
		
		return "";
	}
	
	/* Header */
	protected void addExtraHeaders(HttpUriRequest request) {
		//request.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
		request.setHeader("Host", "rteamtest.appspot.com");
		request.setHeader("Accept", "*/*");
		request.setHeader("Accept-Language", "en-us");
		request.setHeader("Accept-Encoding", "gzip");
		request.setHeader("User-Agent", "rTeam/1.0");
		
		String authString = getAuthenticationHeaderString();
		if (authString != null) {
			request.setHeader("Authorization", authString);
		}
	}
	
	protected String getAuthenticationHeaderString() {
		if (getTokenStorage() == null || getTokenStorage().getUserToken() == null || getTokenStorage().getUserToken().length() == 0) {
			return null;
		}
		
		return "Basic " + Base64Utils.encode(("rTeamLogin:" + getTokenStorage().getUserToken()).getBytes());
	}
	
	/* Converting to/from JSON */
	protected JSONObject toJson(String value) {
		if (value == null) return null;
		try {
			return new JSONObject(value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	protected HttpEntity toEntity(JSONObject value) {
		if (value == null) return null;
		try {
			StringEntity paramsEntity = new StringEntity(value.toString());
			paramsEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			return paramsEntity;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	protected HttpEntity toEntity(String value) {
		if (value == null) return null;
		try {
			return new StringEntity(value);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	protected IUserTokenStorage getTokenStorage() {
		return _tokenStorage;
	}	
	
	/* Debugging */	
	private void logInfo(String messageFormat, Object... args) {
		RTeamLog.i(LOG_SUFFIX, messageFormat, args);
	}
	private void logError(String protocol, String url, Throwable error) {
		RTeamLog.e(LOG_SUFFIX, "Error attempting to %s '%s'.", protocol, url);
		logError(error);
	}
	
	private void logError(Throwable error) {
		RTeamLog.e(LOG_SUFFIX, "Error: %s.", error.toString());
		RTeamLog.e(LOG_SUFFIX, "Error Messages: %s.", error.getMessage());
		RTeamLog.e(LOG_SUFFIX, "Localized Error Message: %s.", error.getLocalizedMessage());
		RTeamLog.e(LOG_SUFFIX, "Stack Trace: %s", getStackTrace(error));

		if (error.getCause() != null) {
			RTeamLog.e(LOG_SUFFIX, "Caused by: %s", error.getCause());
			logError(error.getCause());
		}		
	}
	
	private String getStackTrace(Throwable error) {
		String stacktrace = "";
		for (StackTraceElement element : error.getStackTrace()) {
			stacktrace += "\n\t" + element.toString();
		}
		return stacktrace;
	}
}

