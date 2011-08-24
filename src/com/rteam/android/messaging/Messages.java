package com.rteam.android.messaging;

import com.rteam.android.R;
import com.rteam.android.common.RTeamTabActivity;
import com.rteam.android.common.TabInfo;


public class Messages extends RTeamTabActivity {
	
	@Override
	protected TabInfo[] getTabs() {
		SendMessage.clearOnSend(true);
		return new TabInfo[] {
			new TabInfo(Inbox.class, "inbox", "Inbox", R.drawable.messaging_tab_message),
			new TabInfo(Polls.class, "polls", "Polls", R.drawable.messaging_tab_polls),
			new TabInfo(SentMessages.class, "sent", "Sent", R.drawable.messaging_tab_sent),
			new TabInfo(SendMessage.class, "send", "Send Message", R.drawable.messaging_tab_send)
		};
	}
	
}
