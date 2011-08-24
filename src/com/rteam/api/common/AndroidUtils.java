package com.rteam.api.common;

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
}
