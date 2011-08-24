package com.rteam.android.user;

import com.rteam.android.R;
import com.rteam.android.common.RTeamTabActivity;
import com.rteam.android.common.TabInfo;

public class Settings extends RTeamTabActivity {
	
	@Override
	protected TabInfo[] getTabs() {
		return new TabInfo[] {
				new TabInfo(GlobalSettings.class, "globalsettings", "Global Settings", R.drawable.user_settings),
				new TabInfo(Feedback.class, "feedback", "Feedback", R.drawable.user_feedback)
			};
	}

}
