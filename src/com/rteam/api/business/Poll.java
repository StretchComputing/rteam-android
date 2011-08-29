package com.rteam.api.business;

public class Poll {

	public enum Type {
		YesNo("Yes/No"), Custom("Custom Options");
		
		private String _prettyString;
		
		Type(String prettyString) {
			_prettyString = prettyString;
		}
		
		public String toStringPretty() {
			return _prettyString;
		}
		
		public static Type valueOfPretty(String prettyString) {
			for (Type t : values()) {
				if (t.toStringPretty().equalsIgnoreCase(prettyString)) {
					return t;
				}
			}
			
			return null;
		}
	}
	
	public enum Status {
		Open, Closed;
		
		@Override
		public String toString() { return super.toString().toLowerCase(); }
	}
}
