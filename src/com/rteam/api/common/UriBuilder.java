package com.rteam.api.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class UriBuilder {

	private String _baseUri;
	
	private ArrayList<String> _paths;
	private ArrayList<NameValuePair> _params;
	
	public static UriBuilder create() {
		return new UriBuilder();
	}
	public static UriBuilder create(String baseUri) {
		return new UriBuilder(baseUri);
	}
	
	public UriBuilder() {
		this("");
	}
	
	public UriBuilder(String base) {
		_baseUri = base;
		_paths = new ArrayList<String>();
		_params = new ArrayList<NameValuePair>();
	}
	
	///////////////////////////////////////////////////////////////////
	/// Exposed Members
	
	public UriBuilder addPath(String path) {
		_paths.add(path);
		return this;
	}
	
	public UriBuilder addPathIf(String path, boolean condition) {
		if (condition) return addPath(path);
		return this;
	}
	
	public UriBuilder addParam(String name, String value) {
		_params.add(new BasicNameValuePair(name, value));
		return this;
	}
	
	public UriBuilder addParams(ArrayList<BasicNameValuePair> params) {
		_params.addAll(params);
		return this;
	}
	
	public UriBuilder addParamIf(String name, String value, boolean condition) {
		if (condition) return addParam(name, value);
		return this;
	}
	
	
	@Override
	public String toString() { 
		StringBuilder uri = new StringBuilder(_baseUri);
		
		boolean first = true;
		for (String path : _paths) {
			uri.append(first ? "" : "/").append(urlEncode(path));
			first = false;
		}
		
		first = true;
		for (NameValuePair param : _params) {
			uri.append(first ? "?" : "&").append(urlEncode(param.getName())).append("=").append(urlEncode(param.getValue()));
			first = false;
		}
		
		return uri.toString();
	}
	
	protected String getUrlEncoding() { return "UTF-8"; }
	protected String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, getUrlEncoding());
		} catch (UnsupportedEncodingException e) {
			return value;
		} catch (NullPointerException e) {
			return "";
		}
	}
	
}
