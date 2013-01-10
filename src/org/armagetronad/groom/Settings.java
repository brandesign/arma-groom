package org.armagetronad.groom;

import org.armagetronad.groom.content.UpdateDataService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	public static final String KEY_PREF_DISPLAY_EMPTY_SERVERS = "pref_display_empty_servers";
	public static final String KEY_PREF_ORDER_BY_GID = "pref_order_by_gid";
	public static final String KEY_PREF_AUTO_UPDATE = "pref_auto_update";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (KEY_PREF_DISPLAY_EMPTY_SERVERS.equals(key)) {
			Log.d(Constants.TAG, "Changing key \""
					+ KEY_PREF_DISPLAY_EMPTY_SERVERS + "\" value");
			// TODO reset servers list
		} else if (KEY_PREF_ORDER_BY_GID.equals(key)) {
			Log.d(Constants.TAG, "Changing key \"" + KEY_PREF_ORDER_BY_GID
					+ "\" value");
			// TODO reset players list
		} else if (KEY_PREF_AUTO_UPDATE.equals(key)) {
			if (sharedPreferences.getBoolean(KEY_PREF_AUTO_UPDATE, false)) {
				Log.d(Constants.TAG, "Activated \"" + KEY_PREF_AUTO_UPDATE
						+ "\"");
				Intent start = new Intent(this,
						UpdateDataService.class);
				start.setAction(UpdateDataService.START);
				startService(start);
			} else {
				Log.d(Constants.TAG, "Disabled \"" + KEY_PREF_AUTO_UPDATE
						+ "\"");
				Intent stop = new Intent(this,
						UpdateDataService.class);
				stop.setAction(UpdateDataService.STOP);
				startService(stop);
			}
		}

	}
}
