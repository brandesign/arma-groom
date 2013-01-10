package org.armagetronad.groom.content;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.armagetronad.groom.Constants;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class UpdateDataService extends IntentService {
	public final static String STOP = "STOP";
	public final static String START = "START";

	private static Handler handler = new Handler();
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			new UpdateDatabaseTask(getApplicationContext(), false).execute();
			handler.postDelayed(this, Constants.AUTO_UPDATE_DELAY);
		}
	};

	public UpdateDataService() {
		super("UpdateDataService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(Constants.TAG, "Launching automatic updates intent");

		if (START.equals(intent.getAction())) {
			Log.d(Constants.TAG, "starting the update task");
			handler.postDelayed(runnable, 100);
			
		} else if (STOP.equals(intent.getAction())) {
			Log.d(Constants.TAG, "stopping the update task");
			// TODO may not work, check once issue #24 is solved
			handler.removeCallbacks(runnable);
		}
	}

}
