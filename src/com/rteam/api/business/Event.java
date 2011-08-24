package com.rteam.api.business;

public class Event {
	public enum Type {
		Game, Practice, Generic, None, All;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
		
		public String toPrettyString() { return super.toString(); }
	}
}
