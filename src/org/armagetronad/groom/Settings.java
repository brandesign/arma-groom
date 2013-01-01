package org.armagetronad.groom;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public static final String KEY_PREF_DISPLAY_EMPTY_SERVERS = "pref_display_empty_servers";
	public static final String KEY_PREF_ORDER_BY_GID = "pref_order_by_gid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key == KEY_PREF_DISPLAY_EMPTY_SERVERS) {
			// TODO reset servers list
		} else if (key == KEY_PREF_ORDER_BY_GID) {
			// TODO reset players list
		}
		
	}
}
