package com.rteam.api.common;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {

	public static Bitmap getBitmapFrom(String encodedString) {
		if (StringUtils.isNullOrEmpty(encodedString)) return null;
		
		try {
			byte[] decodedBytes = Base64Utils.decode(encodedString); 
			return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
		} catch(Exception e) {}
		return null;
	}
	
	public static String getEncodedStringFrom(Bitmap bitmap) {
		if (bitmap == null) return null;
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
			bos.flush();
			byte[] encodedBytes = bos.toByteArray();
			bos.close();
			return Base64Utils.encode(encodedBytes);
		} catch(Exception e) {}
		return null;
	}
}
