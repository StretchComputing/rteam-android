package com.rteam.android.common;

import org.acra.*;
import org.acra.annotation.*;

import com.rteam.android.EventService;
import com.rteam.android.R;

import android.app.Application;
import android.content.Intent;

@ReportsCrashes(formKey = "dEhfMDBoV01hSThXWWIyaWVMd3Zvamc6MQ",
				mode = ReportingInteractionMode.TOAST,
				resToastText = R.string.crash_toast_text) 
public class RTeamApplication extends Application {
	
	@Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        startService(new Intent(this, EventService.class));
        super.onCreate();
    }
}
