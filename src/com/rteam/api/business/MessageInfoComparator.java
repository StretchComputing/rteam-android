package com.rteam.api.business;

import java.util.Comparator;

public class MessageInfoComparator implements Comparator<MessageInfo> {
	
	// TODO : Should I make it sort by anything specific?  or always receive date?

	@Override
	public int compare(MessageInfo first, MessageInfo second) {
		if (first == second) return 0;
		if (first == null) return -1;
		if (second == null) return 1;

		return first.receivedDate().compareTo(second.receivedDate());
	}

}
