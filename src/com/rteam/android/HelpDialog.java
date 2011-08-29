package com.rteam.android;

import com.rteam.android.common.HelpProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

public class HelpDialog {
	
	private Context _context;
	
	private View _view;
	private ExpandableListView _listHelp;
	
	private HelpProvider _provider;
	
	public HelpDialog(Context context, HelpProvider provider) {
		_context = context;
		_provider = provider;
		initializeView();
	}
	
	
	private void initializeView() {
		_view = LayoutInflater.from(_context).inflate(R.layout.dlg_help, null);
		_listHelp = (ExpandableListView)_view.findViewById(R.id.listHelp);
		_listHelp.setAdapter(new HelpDialogAdapter(_provider, _context));
	}

	
	public void showDialog() {
		if (_provider != null && _provider.getHelp().size() > 0) {
			new AlertDialog.Builder(_context)
					.setTitle("rTeam - Help")
					.setView(_view)
					.setPositiveButton("Close", null)
					.show();
			_listHelp.expandGroup(0);
		}
	}
}
