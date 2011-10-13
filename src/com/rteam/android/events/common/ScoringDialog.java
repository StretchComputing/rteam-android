package com.rteam.android.events.common;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.api.GamesResource;
import com.rteam.api.GamesResource.UpdateGameResponse;
import com.rteam.api.business.Game;
import com.rteam.api.business.Game.GameInterval;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoringDialog {
	
	public interface ScoresUpdatedHandler {
		public void scoresUpdated();
	}
	
	private ScoresUpdatedHandler _updateHandler;

	private Game _game;
	
	private LayoutInflater _layoutInflater;
	private View _scoreView;
	
	private Button _btnUsPlus3;
	private Button _btnUsPlus2;
	private Button _btnUsPlus1;
	private Button _btnUsMinus1;
	
	private Button _btnThemPlus3;
	private Button _btnThemPlus2;
	private Button _btnThemPlus1;
	private Button _btnThemMinus1;
	
	private Button _btnNextQuarter;
	private Button _btnPrevQuarter;
	
	private TextView _lblScore;
	private TextView _lblQuarter;
	
	public ScoringDialog(Context context, Game game, ScoresUpdatedHandler updateHandler) {
		_layoutInflater = LayoutInflater.from(context);
		_game = game;
		_updateHandler = updateHandler;
		
		initializeScoreView();
	}
	
	private void initializeScoreView() {
		_scoreView = _layoutInflater.inflate(R.layout.dlg_scoring, null);
		
		_btnUsPlus3 = (Button) _scoreView.findViewById(R.id.btnUsPlus3);
		_btnUsPlus2 = (Button) _scoreView.findViewById(R.id.btnUsPlus2);
		_btnUsPlus1 = (Button) _scoreView.findViewById(R.id.btnUsPlus1);
		_btnUsMinus1 = (Button) _scoreView.findViewById(R.id.btnUsMinus1);
		_btnThemPlus3 = (Button) _scoreView.findViewById(R.id.btnThemPlus3);
		_btnThemPlus2 = (Button) _scoreView.findViewById(R.id.btnThemPlus2);
		_btnThemPlus1 = (Button) _scoreView.findViewById(R.id.btnThemPlus1);
		_btnThemMinus1 = (Button) _scoreView.findViewById(R.id.btnThemMinus1);
		
		_lblScore = (TextView) _scoreView.findViewById(R.id.lblScore);
		_lblQuarter = (TextView) _scoreView.findViewById(R.id.lblQuarter);
		
		_btnPrevQuarter = (Button) _scoreView.findViewById(R.id.btnPrevQuarter);
		_btnNextQuarter = (Button) _scoreView.findViewById(R.id.btnNextQuarter);
		
		_btnUsPlus3.setOnClickListener(_clickListener);
		_btnUsPlus2.setOnClickListener(_clickListener);
		_btnUsPlus1.setOnClickListener(_clickListener);
		_btnUsMinus1.setOnClickListener(_clickListener);
		_btnThemPlus3.setOnClickListener(_clickListener);
		_btnThemPlus2.setOnClickListener(_clickListener);
		_btnThemPlus1.setOnClickListener(_clickListener);
		_btnThemMinus1.setOnClickListener(_clickListener);
		
		_btnPrevQuarter.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { prevQuarter(); }
		});
		_btnNextQuarter.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { nextQuarter(); }
		});
		
		bindView();
	}
	
	private void prevQuarter() {
		_game.interval().decrease();
		bindView();
	}
	
	private void nextQuarter() {
		_game.interval().increase();
		bindView();
	}
	
	private void bindView() {
		_lblScore.setText(String.format("%s  -  %s", _game.scoreUs(), _game.scoreThem()));
		_lblQuarter.setText(String.format("Interval: %s", _game.interval().toString()));
		// TODO : Interval -- localize it for the game type?
	}
	
	private View.OnClickListener _clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v == _btnUsPlus3) 	 addUs(3);
			if (v == _btnUsPlus2) 	 addUs(2);
			if (v == _btnUsPlus1) 	 addUs(1);
			if (v == _btnUsMinus1)	 addUs(-1);
			if (v == _btnThemPlus3)  addThem(3);
			if (v == _btnThemPlus2)  addThem(2);
			if (v == _btnThemPlus1)  addThem(1);
			if (v == _btnThemMinus1) addThem(-1);
			bindView();
		}
	};
	
	
	private void addUs(int amount)   { _game.scoreUs(_game.scoreUs() + amount); }
	private void addThem(int amount) { _game.scoreThem(_game.scoreThem() + amount); }
	
	private void saveScore() {
		_updateHandler.scoresUpdated();
		
		CustomTitle.setLoading(true, "Saving...");
		GamesResource.instance().update(new Game.Update(_game), new GamesResource.UpdateGameResponseHandler() {
			@Override public void finish(UpdateGameResponse response) { CustomTitle.setLoading(false); }
		});
	}
	
	private void setGameOver() {
		_game.interval(GameInterval.GameOver);
		saveScore();
	}
	
	public void showDialog() {
		new AlertDialog.Builder(_layoutInflater.getContext())
				.setTitle("Game Scoring")
				.setView(_scoreView)
				.setPositiveButton("Done", new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { saveScore(); }})
				.setNeutralButton("Game Over", new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) { setGameOver(); }})
				.show();
	}	
}
