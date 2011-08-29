package com.rteam.android.teams.common;

import java.util.Calendar;
import java.util.Date;

import com.rteam.android.R;
import com.rteam.android.common.CustomTitle;
import com.rteam.android.common.DateTimeDialog;
import com.rteam.api.GamesResource;
import com.rteam.api.PracticeResource;
import com.rteam.api.GamesResource.CreateGameResponse;
import com.rteam.api.PracticeResource.CreatePracticeResponse;
import com.rteam.api.business.Event;
import com.rteam.api.business.EventBase;
import com.rteam.api.business.Game;
import com.rteam.api.business.Practice;
import com.rteam.api.business.Team;
import com.rteam.api.business.Message.NotificationType;
import com.rteam.api.common.DateUtils;
import com.rteam.api.common.StringUtils;
import com.rteam.api.common.TimeZoneUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class AddEventDialog extends Dialog {
	
	public interface AddClickedHandler {
		public void addClicked(EventBase event);
	}
	
	/////////////////////////////////////////////////////////////
	//// Members
	
	private ToggleButton _btnTypeGame;
	private ToggleButton _btnTypePractice;
	private ToggleButton _btnTypeOther;
	
	private EditText _textStartTime;
	private EditText _textOpponent;
	private EditText _textDescription;
	private EditText _textDuration;
	
	private Button _btnAdd;
	private Button _btnCancel;
	
	private Event.Type _eventType;
	private Date _startDateTime;
	private Event.Type _defaultEventType;
	
	private Team _team;
	
	private AddClickedHandler _addClickHandler;
	
	/////////////////////////////////////////////////////////////
	//// .ctor	

	public AddEventDialog(Context context, Team team, AddClickedHandler addClickHandler) {
		this(context, team, new Date(), addClickHandler);
	}
	public AddEventDialog(Context context, Team team, Date defaultDate, AddClickedHandler addClickHandler) {
		this(context, team, defaultDate, Event.Type.Game, addClickHandler);
	}
	
	public AddEventDialog(Context context, Team team, Date defaultDate, Event.Type defaultEventType, AddClickedHandler addClickHandler) {
		super(context);
		
		_startDateTime = defaultDate;
		_team = team;
		_addClickHandler = addClickHandler;
		_defaultEventType = defaultEventType;
		
		initializeView();
	}

	/////////////////////////////////////////////////////////////
	//// Initialization
	
	private void initializeView() {
		setContentView(R.layout.teams_events_add);
		setTitle("Add Event");
		
		_btnTypeGame = (ToggleButton) findViewById(R.id.btnTypeGame);
		_btnTypePractice = (ToggleButton) findViewById(R.id.btnTypePractice);
		_btnTypeOther = (ToggleButton) findViewById(R.id.btnTypeOther);
		
		//_dateEventDate = (DatePicker) findViewById(R.id.dateEventDate);
		_textStartTime = (EditText) findViewById(R.id.textStartTime);
		_textOpponent = (EditText) findViewById(R.id.textOpponent);
		_textDescription = (EditText) findViewById(R.id.textDescription);
		_textDuration = (EditText) findViewById(R.id.textDuration);
		
		_btnAdd = (Button) findViewById(R.id.btnAdd);
		_btnCancel = (Button) findViewById(R.id.btnCancel);
		
		setEventType(_defaultEventType);
		setStartDateTime(_startDateTime);
		
		_textStartTime.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setStartDateTime(); }
		});
		
		_btnTypeGame.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setEventType(Event.Type.Game); }
		});
		_btnTypePractice.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setEventType(Event.Type.Practice); }
		});
		_btnTypeOther.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { setEventType(Event.Type.Generic); }
		});
		
		_btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { addClicked(); }
		});
		_btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { cancel(); }
		});
		
		bindButtons();
		_textOpponent.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		_textDescription.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
		_textDuration.setOnKeyListener(new View.OnKeyListener() {
			@Override public boolean onKey(View v, int keyCode, KeyEvent event) { bindButtons(); return false; }
		});
	}
	
	/////////////////////////////////////////////////////////////
	//// Event Helpers
	
	
	private void bindButtons() {
		_btnAdd.setEnabled(StringUtils.hasText(_textStartTime));
	}
	
	private void setStartDateTime() {
		new DateTimeDialog(getContext(), _startDateTime, new DateTimeDialog.DoneHandler() {
			@Override public void onDone(Date dateSelected) { setStartDateTime(dateSelected); }
		}).show();
	}
	
	private void setStartDateTime(Date startDateTime) {
		_startDateTime = startDateTime;
		_textStartTime.setText(DateUtils.toPrettyString(_startDateTime));
		bindButtons();
	}
	
	private void setEventType(Event.Type eventType) {
		_eventType = eventType;
		
		_btnTypeGame.setChecked(_eventType == Event.Type.Game);
		_btnTypePractice.setChecked(_eventType == Event.Type.Practice);
		_btnTypeOther.setChecked(_eventType == Event.Type.Generic);
	}
	
	private void addClicked() {
		EventBase event = getEventInfo();
		if (event.isGame()) {
			createGame((Game) event);
		}
		else {
			createPractice((Practice) event);
		}
	}
	
	private void createGame(final Game event) {
		Game.Create createInfo = event.new Create();
		createInfo.Game(event);
		createInfo.notificationType(NotificationType.None);
		createInfo.timeZone(TimeZoneUtils.getTimeZone());
		
		CustomTitle.setLoading(true, "Creating game...");
		new GamesResource()
			.create(createInfo, new GamesResource.CreateGameResponseHandler() {
				@Override
				public void finish(CreateGameResponse response) {
					CustomTitle.setLoading(false);
					if (response.showError(getContext())) {
						finishAdding(event);
					}
				}
			});
	}
	
	private void createPractice(final Practice practice) {
		Practice.Create createInfo = practice.new Create();
		createInfo.Practice(practice);
		createInfo.notificationType(NotificationType.None);
		createInfo.timeZone(TimeZoneUtils.getTimeZone());
		
		CustomTitle.setLoading(true, "Creating practice...");
		new PracticeResource()
			.create(createInfo, new PracticeResource.CreatePracticeResponseHandler() {
				@Override
				public void finish(CreatePracticeResponse response) {
					CustomTitle.setLoading(false);
					if (response.showError(getContext())) {
						practice.practiceId(response.practiceId());
						finishAdding(practice);
					}
				}
			});
	}
	
	private void finishAdding(EventBase event) {
		_addClickHandler.addClicked(event);
		dismiss();
	}
	
	private EventBase getEventInfo() {
		if (_btnTypeGame.isChecked()) {
			return getGameEventInfo();
		}
		return getPracticeEventInfo();
	}
	
	private EventBase getGameEventInfo() {
		return setCommonProperties(new Game());
	}
	
	private EventBase getPracticeEventInfo() {
		return setCommonProperties(new Practice());
	}
	
	private EventBase setCommonProperties(EventBase event) {
		event.eventType(_eventType);
		event.opponent(_textOpponent.getText().toString());
		event.description(_textDescription.getText().toString());
		event.startDate(_startDateTime);
		
		event.teamId(_team.teamId());
		event.teamName(_team.teamName());
		if (!StringUtils.isNullOrEmpty(_textDuration.getText().toString())) {
			Calendar c = Calendar.getInstance();
			c.setTime(event.startDate());
			c.add(Calendar.MINUTE, Integer.parseInt(_textDuration.getText().toString()));
			event.endDate(c.getTime());
		}
		
		return event;
	}
}
