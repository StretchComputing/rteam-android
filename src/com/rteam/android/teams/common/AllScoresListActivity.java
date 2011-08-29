package com.rteam.android.teams.common;

import java.util.List;

import com.rteam.android.R;
import com.rteam.api.business.Game;
import com.rteam.api.common.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AllScoresListActivity extends BaseAdapter {
	
	private LayoutInflater _inflater;
	private List<Game> _games;	
	
	public AllScoresListActivity(Context context, List<Game> games) {
		_inflater = LayoutInflater.from(context);
		_games = games;
	}
	
	@Override public int getCount() { return _games.size(); }
	@Override public Object getItem(int index) { return _games.get(index); }
	@Override public long getItemId(int index) { return index; }

	@Override
	public View getView(int index, View convertView, ViewGroup group) {
		Game game = (Game) getItem(index);
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = _inflater.inflate(R.layout.teams_allscores_listitem, null);
			holder = new ViewHolder();
			
			holder.lblTeam1Score = (TextView) convertView.findViewById(R.id.lblTeam1Score);
			holder.lblStatus = (TextView) convertView.findViewById(R.id.lblStatus);
			holder.lblTeam2Score = (TextView) convertView.findViewById(R.id.lblTeam2Score);
			holder.lblTeam1 = (TextView) convertView.findViewById(R.id.lblTeam1);
			holder.lblDate = (TextView) convertView.findViewById(R.id.lblGameDate);
			holder.lblTeam2 = (TextView) convertView.findViewById(R.id.lblTeam2);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		bindView(holder, game);
		
		return convertView;
	}

	private void bindView(ViewHolder view, Game game) {
		view.lblTeam1Score.setText(game.scoreUs() >= 0 ? Integer.toString(game.scoreUs()) : "-");
		view.lblTeam2Score.setText(game.scoreThem() >= 0 ? Integer.toString(game.scoreThem()) : "-");
		view.lblTeam1.setText(game.teamName());
		view.lblTeam2.setText(game.opponent());
		view.lblDate.setText(DateUtils.toShortString(game.startDate()));
	}
	
	static class ViewHolder {
		TextView lblTeam1Score;
		TextView lblStatus;
		TextView lblTeam2Score;
		TextView lblTeam1;
		TextView lblDate;
		TextView lblTeam2;
	}
}
