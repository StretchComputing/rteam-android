package com.rteam.android.common;

import org.acra.*;
import org.acra.annotation.*;

import com.rteam.android.EventService;
import com.rteam.android.R;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

@ReportsCrashes(formKey = "dEhfMDBoV01hSThXWWIyaWVMd3Zvamc6MQ",
				mode = ReportingInteractionMode.TOAST,
				resToastText = R.string.crash_toast_text) 
public class RTeamApplication extends Application {
	
	private static Context _appContext;
	public static Context getAppContext() { return _appContext; }
	
	private RTeamAnalytics _tracker;
	
	@Override
    public void onCreate() {
		_appContext = getApplicationContext();
		
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        startService(new Intent(this, EventService.class));
        
        _tracker = new RTeamAnalytics(this);
        _tracker.trackApplicationLaunched();
                
        super.onCreate();
    }
	
	@Override
	public void onTerminate()
	{
		_tracker.dispose();
		super.onTerminate();
	}
}
