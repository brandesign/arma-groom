package org.armagetronad.groom.content;

import android.provider.BaseColumns;
public class ArmaContent {

	public ArmaContent() {
		
	}
	
	public static final class Server implements BaseColumns {
		public static final String SERVER_NAME = "SERVER_NAME";
		public static final String SERVER_IP = "SERVER_IP";
		public static final String SERVER_PORT = "SERVER_PORT";
		public static final String SERVER_VERSION = "SERVER_VERSION";
		public static final String SERVER_VERSION_MIN = "SERVER_VERSION_MIN";
		public static final String SERVER_VERSION_MAX = "SERVER_VERSION_MAX";
		public static final String SERVER_DESCRIPTION = "SERVER_DESCRIPTION";
		public static final String SERVER_DESCRIPTION_COLORS = "SERVER_DESCRIPTION_COLORS";
		public static final String SERVER_NUMPLAYERS = "SERVER_NUMPLAYERS";
		public static final String SERVER_MAXPLAYERS = "SERVER_MAXPLAYERS";
		public static final String SERVER_URL = "SERVER_URL";
		public static final String SERVER_URL_COLORS = "SERVER_URL_COLORS";
	}
	
	public static final class Player implements BaseColumns {
		public static final String PLAYER_NAME = "PLAYER_NAME";
		public static final String PLAYER_SERVER_ID = "PLAYER_SERVER_ID";
		public static final String PLAYER_GLOBAL_ID = "PLAYER_GID";
	}
	
	public static final class Setting implements BaseColumns {
		public static final String SETTING_PARAM = "PARAM";
		public static final String SETTING_VALUE = "VALUE";
		public static final String KEY_DATE = "ServersFeed.Date";
	}
	
}
