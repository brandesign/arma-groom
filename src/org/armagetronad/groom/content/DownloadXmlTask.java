package org.armagetronad.groom.content;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.armagetronad.groom.ArmaUtils;
import org.armagetronad.groom.Constants;
import org.armagetronad.groom.RuntimeData;
import org.armagetronad.groom.content.ArmaContent.Player;
import org.armagetronad.groom.content.ArmaContent.Server;
import org.armagetronad.groom.content.ArmaContent.Setting;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class DownloadXmlTask extends AsyncTask<String, Void, Void> {

	private final Context context;
	private final Date date;

	public DownloadXmlTask(Context ctx, Date date) {
		this.context = ctx;
		this.date = date;
	}

	@Override
	protected Void doInBackground(String... urls) {
		ArmaUtils.displayMessage(context,
				"Downloading feed from the internet...", Toast.LENGTH_LONG);
		Log.i(Constants.TAG, "Calling DownloadXmlTask with " + urls.length
				+ " urls");
		ServersFeed ret = null;
		for (String url : urls) {
			Log.i(Constants.TAG, "Calling URL " + url);
			ret = loadXmlFromNetwork(url);
			if (ret != null) {
				Log.i(Constants.TAG, "Got data, going on...");
				break;
			}
		}
		if (ret == null) {
			Log.e(Constants.TAG, "no data received, cancelling");
			cancel(true);
		}
		processResult(ret);
		return null;
	}

	protected void processResult(ServersFeed result) {

		Log.i(Constants.TAG,
				"Download from server & parsing finished. Now adding to database");
		ContentResolver resolver = context.getContentResolver();
		ContentProviderClient client = resolver
				.acquireContentProviderClient(ArmaProvider.URI_SERVERS);
		try {
			client.delete(Uri.EMPTY, null, null);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ContentValues values;
		Uri serverUri = null;
		String serverId;
		for (ServerEntity server : result.getServers()) {
			serverId = "-1";
			if (isCancelled()) {
				break;
			}
			values = new ContentValues();
			values.put(Server.SERVER_NAME, server.name);
			values.put(Server.SERVER_IP, server.ip);
			values.put(Server.SERVER_PORT, server.port);
			values.put(Server.SERVER_VERSION, server.version);
			values.put(Server.SERVER_VERSION_MIN, server.version_min);
			values.put(Server.SERVER_VERSION_MAX, server.version_max);
			values.put(Server.SERVER_DESCRIPTION,
					ArmaUtils.uncolourize(server.description));
			values.put(Server.SERVER_DESCRIPTION_COLORS, server.description);
			values.put(Server.SERVER_NUMPLAYERS, server.numplayers);
			values.put(Server.SERVER_MAXPLAYERS, server.maxplayers);
			values.put(Server.SERVER_URL, ArmaUtils.uncolourize(server.url));
			values.put(Server.SERVER_URL_COLORS, server.url);
			try {
				serverUri = client.insert(ArmaProvider.URI_SERVERS, values);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (serverUri != null) {
				serverId = serverUri.getLastPathSegment();
			}
			for (PlayerEntity player : server.players) {
				values = new ContentValues();
				values.put(Player.PLAYER_GLOBAL_ID, player.gid);
				values.put(Player.PLAYER_NAME, player.name);
				values.put(Player.PLAYER_SERVER_ID, serverId);
				try {
					client.insert(ArmaProvider.URI_PLAYERS, values);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		ContentValues valuesSettings = new ContentValues();
		valuesSettings.put(Setting.SETTING_PARAM, Setting.KEY_DATE);
		valuesSettings.put(Setting.SETTING_VALUE, new SimpleDateFormat(
				ArmaProvider.DATE_FORMAT).format(result.getDate()));
		try {
			client.insert(ArmaProvider.URI_SETTINGS, valuesSettings);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		client.release();
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.i(Constants.TAG, "Data successfully added to db");
		if (RuntimeData.forceUpdateAllLoaders()) {
			ArmaUtils.displayMessage(context, "Data successfully updated",
					Toast.LENGTH_LONG);
		} else {
			ArmaUtils
					.displayMessage(
							context,
							"Failure in updating data. An applicaiton restart may solve the issue.",
							Toast.LENGTH_LONG);
		}
	}

	private ServersFeed loadXmlFromNetwork(String urlString) {
		InputStream stream = null;
		ServersFeed serversFeed = null;

		ArmaserversXmlParser armaXmlParser = new ArmaserversXmlParser(date);

		HttpURLConnection conn = null;
		try {
			conn = ArmaUtils.getConnection(urlString, 200);
			stream = conn.getInputStream();
			serversFeed = armaXmlParser.parse(stream);
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} catch (IOException e) {
			Log.e(Constants.TAG, "got IOException " + e.getMessage());
			return null;
		} catch (XmlPullParserException e) {
			Log.e(Constants.TAG, "got XmlPullParserException " + e.getMessage());
			return null;
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception e) {
				; // do nothing
			}
			try {
				if (conn != null) {
					conn.disconnect();
				}
			} catch (Exception e) {
				; // do nothing
			}
		}
		return serversFeed;
	}

}
