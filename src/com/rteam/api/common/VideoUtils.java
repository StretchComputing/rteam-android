package com.rteam.api.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.rteam.android.common.RTeamLog;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;

public class VideoUtils {

	
	public static String getEncodedVideoFrom(ContentResolver contentResolver, String path) {
		try {
			Uri uri = Uri.parse(path);
			
			RTeamLog.i("Trying to encode video file: %s", path);
			AssetFileDescriptor videoAsset = contentResolver.openAssetFileDescriptor(uri, "r");
		    FileInputStream fis = videoAsset.createInputStream();
		    ByteArrayOutputStream ous = new ByteArrayOutputStream();
		    
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = fis.read(buf)) > 0) {
		    	ous.write(buf, 0, len);	        
		    }
		    RTeamLog.i("Video file contained %d bytes.", ous.size());
		    return Base64Utils.encode(ous.toByteArray());
		} catch(Exception e) {
			RTeamLog.i("Uh OH: %s", e.toString());
		}
		return null;
	}
	
	public static String writeEncodedVideo(String encodedVideoString, String videoId) {
		try {
			File tmpFile = new File(getDirectory(), String.format("rteam_%s.3gp", videoId));
			
			RTeamLog.i("Storing Video File of length %d to: %s", encodedVideoString.length(), tmpFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(tmpFile);
			
			fos.write(Base64Utils.decode(encodedVideoString));
			fos.close();
			
			RTeamLog.i("Done encoding!");
			return tmpFile.getAbsolutePath();
		} catch(Exception e) {
			RTeamLog.i("UHHH OHHH: %s", e.toString());
		}
		return null;
	}
	
	private static File getDirectory() {
		File dir;
		try {
			dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
			if (dir.exists()) return dir;
		} catch(Exception e) {}
		try {
			dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			if (dir.exists()) return dir;
		} catch(Exception e) {}
		try {
			dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			if (dir.exists()) return dir;
		} catch(Exception e) {}
		return Environment.getExternalStorageDirectory();
	}
}
