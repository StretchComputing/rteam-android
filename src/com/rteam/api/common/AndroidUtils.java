package com.rteam.api.common;

import java.io.File;

import com.rteam.android.common.RTeamApplication;

import android.widget.EditText;

public class AndroidUtils {
	public static boolean hasText(EditText text) {
		return text.getText().length() > 0;
	}	

	public static boolean allHaveText(EditText ... texts) {
		for (EditText text : texts) {
			if (!hasText(text)) return false;
		}
		return true;
	}
	
	public static File getVideoDirectory() {
		File videoDirectory = new File(getRTeamDirectory(), "videos");
		if (!videoDirectory.exists()) {
			videoDirectory.mkdirs();
		}
		
		return videoDirectory;
	}
	
	public static File getImageDirectory() {
		File imageDirectory = new File(getRTeamDirectory(), "images");
		if (!imageDirectory.exists()) {
			imageDirectory.mkdirs();
		}
		
		return imageDirectory;
	}
	
	public static File getRTeamDirectory() {
		File rteamDirectory = new File(RTeamApplication.getAppContext().getFilesDir(), "rTeam");
		if (!rteamDirectory.exists()) {
			rteamDirectory.mkdirs();
		}
		
		return rteamDirectory;
	}
	
	
}
