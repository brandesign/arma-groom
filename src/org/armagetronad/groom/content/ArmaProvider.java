package org.armagetronad.groom.content;

import org.armagetronad.groom.content.ArmaContent.Player;
import org.armagetronad.groom.content.ArmaContent.Server;
import org.armagetronad.groom.content.ArmaContent.Setting;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class ArmaProvider extends ContentProvider {

	public static final String CONTENT_PROVIDER_DB_NAME = "armagroom.db";

	public static final int CONTENT_PROVIDER_DB_VERSION = 7;

	public static final String CONTENT_PROVIDER_TABLE_NAME_SERVERS = "servers";

	public static final String CONTENT_PROVIDER_TABLE_NAME_PLAYERS = "players";
	
	public static final String CONTENT_PROVIDER_TABLE_NAME_SETTINGS = "settings";
	

	public static final String AUTHORITY_URI = "org.armagetronad.groom.provider"; // TODO manifest

	public static final Uri URI_SERVERS = Uri.parse("content://" + AUTHORITY_URI + "/server");
	

	public static final Uri URI_PLAYERS = Uri.parse("content://" + AUTHORITY_URI + "/player");

	private static final int URI_MATCHER_PLAYERS = 1;
	private static final int URI_MATCHER_PLAYER_IN_SERVER_FILTER = 2;
	private static final int URI_MATCHER_SERVERS = 3;
	private static final int URI_MATCHER_SERVER_ID = 4;
	private static final int URI_MATCHER_SETTINGS = 5;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY_URI, "player", URI_MATCHER_PLAYERS);
		sURIMatcher.addURI(AUTHORITY_URI, "player/inserver/#", URI_MATCHER_PLAYER_IN_SERVER_FILTER);
		sURIMatcher.addURI(AUTHORITY_URI, "server", URI_MATCHER_SERVERS);
		sURIMatcher.addURI(AUTHORITY_URI, "server/#", URI_MATCHER_SERVER_ID);
		sURIMatcher.addURI(AUTHORITY_URI, "settings", URI_MATCHER_SETTINGS);
	}

	public static final String CONTENT_PROVIDER_MIME_SUBTYPE_SERVER = "vnd.org.armagetronad.groom.content.server";
	public static final String CONTENT_PROVIDER_MIME_SUBTYPE_PLAYER = "vnd.org.armagetronad.groom.content.player";
	public static final String CONTENT_PROVIDER_MIME_SUBTYPE_SETTING = "vnd.org.armagetronad.groom.content.setting";

	public static final Uri URI_SETTINGS = Uri.parse("content://" + AUTHORITY_URI + "/settings");

	public static final String DATE_FORMAT = "MM-dd-yyyy HH:mm:ss";


	private DatabaseHelper dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		dbHelper.emptyData(dbHelper.getWritableDatabase());
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		StringBuffer result = new StringBuffer("vnd.android.cursor.");

		result.append("item");
		result.append("/");

		switch (sURIMatcher.match(uri)) {
		case URI_MATCHER_PLAYERS:
		case URI_MATCHER_PLAYER_IN_SERVER_FILTER:
			result.append(CONTENT_PROVIDER_MIME_SUBTYPE_PLAYER);
			break;
		case URI_MATCHER_SERVERS:
		case URI_MATCHER_SERVER_ID:
			result.append(CONTENT_PROVIDER_MIME_SUBTYPE_SERVER);
			break;
		case URI_MATCHER_SETTINGS:
			result.append(CONTENT_PROVIDER_MIME_SUBTYPE_SETTING);
			break;
		}
		return result.toString();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		long id;
		switch(sURIMatcher.match(uri)) {
		case URI_MATCHER_SERVERS:
			id = db.insert(CONTENT_PROVIDER_TABLE_NAME_SERVERS, null, values);
			return Uri.parse("content://" + AUTHORITY_URI + "/server/" + id);
		case URI_MATCHER_PLAYERS:
			id = db.insert(CONTENT_PROVIDER_TABLE_NAME_PLAYERS, null, values);
			return Uri.parse("content://" + AUTHORITY_URI + "/player/" + id);
			case URI_MATCHER_SETTINGS:
				db.insert(CONTENT_PROVIDER_TABLE_NAME_SETTINGS, null, values);
				return URI_SETTINGS;
			default:
				return Uri.EMPTY;
		}
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String table = null;
		String[] columns = projection;
		String groupBy = null;
		String having = null;
		String limit = null;

		switch (sURIMatcher.match(uri)) {
		case URI_MATCHER_PLAYERS:
			table = CONTENT_PROVIDER_TABLE_NAME_PLAYERS;
			selection = null;
			selectionArgs = null;
			break;
		case URI_MATCHER_PLAYER_IN_SERVER_FILTER:
			table = CONTENT_PROVIDER_TABLE_NAME_PLAYERS;
			selection = Player.PLAYER_SERVER_ID + " = ?";
			selectionArgs = new String[] {uri.getLastPathSegment()};
			break;
		case URI_MATCHER_SERVERS:
			table = CONTENT_PROVIDER_TABLE_NAME_SERVERS;
			break;
		case URI_MATCHER_SERVER_ID:
			table = CONTENT_PROVIDER_TABLE_NAME_SERVERS;
			selection = Server._ID + " = ?";
			selectionArgs = new String[] {uri.getLastPathSegment()};
			break;
		case URI_MATCHER_SETTINGS:
			table = CONTENT_PROVIDER_TABLE_NAME_SETTINGS;
			break;
		case UriMatcher.NO_MATCH:
			// TODO handle error
		}

		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, sortOrder, limit);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		dbHelper.refreshData(dbHelper.getWritableDatabase());
		return 0;
	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, ArmaProvider.CONTENT_PROVIDER_DB_NAME, null,
					ArmaProvider.CONTENT_PROVIDER_DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Servers
			db.execSQL("CREATE TABLE "
					+ ArmaProvider.CONTENT_PROVIDER_TABLE_NAME_SERVERS + " ("
					+ Server._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Server.SERVER_NAME + " VARCHAR(255),"
					+ Server.SERVER_IP + " VARCHAR(255)," + Server.SERVER_PORT
					+ " INTEGER(5)," + Server.SERVER_VERSION
					+ " VARCHAR(255)," + Server.SERVER_VERSION_MIN
					+ " VARCHAR(255)," + Server.SERVER_VERSION_MAX
					+ " VARCHAR(255)," + Server.SERVER_DESCRIPTION_COLORS
					+ " VARCHAR(255)," + Server.SERVER_DESCRIPTION
					+ " VARCHAR(255)," + Server.SERVER_NUMPLAYERS
					+ " INTEGER(4)," + Server.SERVER_MAXPLAYERS
					+ " INTEGER(4)," + Server.SERVER_URL_COLORS
					+ " VARCHAR(255)," + Server.SERVER_URL + " VARCHAR(255)"
					+ ");");

			// Players
			db.execSQL("CREATE TABLE "
					+ ArmaProvider.CONTENT_PROVIDER_TABLE_NAME_PLAYERS + " ("
					+ Player._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Player.PLAYER_NAME + " VARCHAR(255),"
					+ Player.PLAYER_GLOBAL_ID + " VARCHAR(255),"
					+ Player.PLAYER_SERVER_ID + " VARCHAR(255)" + ");");
			
			db.execSQL("CREATE TABLE "
					+ ArmaProvider.CONTENT_PROVIDER_TABLE_NAME_SETTINGS + " ("
					+ Setting._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ Setting.SETTING_PARAM+ " VARCHAR(255),"
					+ Setting.SETTING_VALUE + " VARCHAR(255)" + ");");

			fillData(db);
		}

		private void fillData(SQLiteDatabase db) {
			
			//(new DownloadXmlTask(getContext(),null)).execute(Constants.URL_XML_FEEDS);

		}
		
		public void refreshData(SQLiteDatabase db) {
			emptyData(db);
			fillData(db);
		}
		
		private void emptyData(SQLiteDatabase db) {
			db.delete(CONTENT_PROVIDER_TABLE_NAME_PLAYERS, null, null);
			db.delete(CONTENT_PROVIDER_TABLE_NAME_SERVERS, null, null);
			db.delete(CONTENT_PROVIDER_TABLE_NAME_SETTINGS, null, null);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "
					+ ArmaProvider.CONTENT_PROVIDER_TABLE_NAME_SERVERS);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ArmaProvider.CONTENT_PROVIDER_TABLE_NAME_PLAYERS);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ArmaProvider.CONTENT_PROVIDER_TABLE_NAME_SETTINGS);

			onCreate(db);
		}

	}

}
