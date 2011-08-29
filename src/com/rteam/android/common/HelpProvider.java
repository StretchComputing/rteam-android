package com.rteam.android.common;

import java.util.ArrayList;
import java.util.List;

public class HelpProvider {
	
	public static class HelpContent {
		private String _title;
		public String getTitle() { return _title; }
		
		private String _content;
		public String getContent() { return _content; }
		
		public HelpContent(String title, String content) {
			_title = title;
			_content = content;
		}
	}
	
	
	private List<HelpContent> _help;
	public List<HelpContent> getHelp() { return _help; }
	
	public HelpProvider(HelpContent... helpContents) {
		_help = new ArrayList<HelpContent>();
		for (HelpContent help : helpContents) {
			_help.add(help);
		}
	}
}
