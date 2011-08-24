package com.rteam.api.business;

public class Message {
	public enum Group {
		Inbox, Outbox, All;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
	
	public enum Status {
		Active, All, Finalized, None;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
	
	public enum Type {
		Poll, Confirm, Plain, Message, None;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
		
		public String toStringPretty() { return super.toString(); }
	}
	
	public enum UpdateStatus {
		Archived, None;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
	
	public enum NotificationType {
		None, Plain, Confirm;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
}
