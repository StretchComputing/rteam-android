package com.rteam.api.common;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtils {
	
	public static interface TimerCallback {
		public void Tick();
	}
	
	private static class Task extends TimerTask {
		private TimerCallback _callback;
		
		public Task(TimerCallback callback) {
			_callback = callback;
		}

		@Override
		public void run() {
			_callback.Tick();
		}
	}

	public static Timer WaitFor(long timeMs, TimerCallback callback) {
		Timer timer = new Timer();
		timer.schedule(new Task(callback), 0, timeMs);
		return timer;
	}
	
}
