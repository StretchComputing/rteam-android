package com.rteam.android.common;

import android.content.pm.PackageManager.NameNotFoundException;

import com.rteam.api.base.IApplicationVersion;

public class RTeamApplicationVersion implements IApplicationVersion {

	private String _applicationVersion;
	
	@Override
	public String getApplicationVersion() {
		if (_applicationVersion == null) {
			try {
				_applicationVersion = RTeamApplication.getAppContext()
										.getPackageManager().getPackageInfo("com.rteam.android", 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			_applicationVersion = "UNKNOWN";
		}
		return _applicationVersion;	
	}

	private static RTeamApplicationVersion _instance;
	public static RTeamApplicationVersion get() {
		if (_instance == null) {
			_instance = new RTeamApplicationVersion();
		}
		return _instance;
	}
}
