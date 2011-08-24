package com.rteam.android.teams;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rteam.android.R;
import com.rteam.android.common.RTeamActivity;
import com.rteam.api.business.Team;

public class AuthorizeTwitter extends RTeamActivity {

	public interface Callback {
		public void doneAuthorizing();
	}
	
	private static Team _team;
	private static String _twitterAuthUrl;
	private static Callback _callback;
	public static void setup(Team team, String twitterAuthUrl, Callback callback) { 
		_team = team;
		_twitterAuthUrl = twitterAuthUrl;
		_callback = callback;
	}
	public static void clear() { setup(null, null, null); }
	
	
	@Override 
	protected String getCustomTitle() { return "rTeam - authorize twitter access"; }
	
	private WebView _webMain;
	
	@Override
	protected void initialize() {
		initializeView();
		bindView();
	}

	private void initializeView() {
		setContentView(R.layout.teams_authorize_twitter);	
		_webMain = (WebView) findViewById(R.id.webMain);
		_webMain.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Uri uri = Uri.parse(url);
				if (uri.getHost().equalsIgnoreCase("rteamtest.appspot.com")) {
					AuthorizeTwitter.this.finish();
					Callback cb = _callback;
					clear();
					cb.doneAuthorizing();
				}
			}
		});
	}
	
	private void bindView() {
		_webMain.loadUrl(_twitterAuthUrl);
	}
}
