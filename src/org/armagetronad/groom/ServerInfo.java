package org.armagetronad.groom;

import org.armagetronad.groom.content.ArmaContent.Player;
import org.armagetronad.groom.content.ArmaProvider;
import org.armagetronad.groom.content.ArmaContent.Server;

import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.RemoteException;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;

public class ServerInfo extends FragmentActivity {

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] SERVER_PROJECTION = new String[] { Server._ID, // 0
			Server.SERVER_NAME, // 1
			Server.SERVER_IP, // 2
			Server.SERVER_PORT, // 3
			Server.SERVER_DESCRIPTION_COLORS, // 4
			Server.SERVER_NUMPLAYERS, // 5
			Server.SERVER_MAXPLAYERS, // 6
			Server.SERVER_URL_COLORS, // 7
			Server.SERVER_VERSION // 8
	};
	public static final String[] PLAYERS_PROJECTION = new String[] {
			Player._ID, // 0
			Player.PLAYER_NAME, // 1
			Player.PLAYER_GLOBAL_ID // 2
	};

	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_NAME_INDEX = 1;
	private static final int PROJECTION_IP_INDEX = 2;
	private static final int PROJECTION_PORT_INDEX = 3;
	private static final int PROJECTION_DESCRIPTION_INDEX = 4;
	private static final int PROJECTION_NUMPLAYERS_INDEX = 5;
	private static final int PROJECTION_MAXPLAYERS_INDEX = 6;
	private static final int PROJECTION_URL_INDEX = 7;
	private static final int PROJECTION_VERSION_INDEX = 8;
	private static final int PROJECTION_PLAYER_ID_INDEX = 0;
	private static final int PROJECTION_PLAYER_NAME_INDEX = 1;
	private static final int PROJECTION_PLAYER_GID_INDEX = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long serverId = getIntent().getExtras().getLong(MainActivity.SERVER_ID);
		ContentResolver resolver = this.getContentResolver();
		ContentProviderClient client = resolver
				.acquireContentProviderClient(ArmaProvider.URI_SERVERS);
		Cursor serverCursor = null;
		Cursor playersCursor = null;
		Uri serverProviderUri = Uri.parse("content://"
				+ ArmaProvider.AUTHORITY_URI + "/server/" + serverId);
		Uri playersProviderUri = Uri.parse("content://"
				+ ArmaProvider.AUTHORITY_URI + "/player/inserver/" + serverId);
		try {
			serverCursor = client.query(serverProviderUri, SERVER_PROJECTION,
					null, null, null);
			playersCursor = client.query(playersProviderUri,
					PLAYERS_PROJECTION, null, null, null);
			serverCursor.moveToFirst();

			String name = null;
			String ip = null;
			String port = null;
			String description = null;
			String numplayers = null;
			String maxplayers = null;
			String url = null;
			String version = null;

			name = serverCursor.getString(PROJECTION_NAME_INDEX);
			ip = serverCursor.getString(PROJECTION_IP_INDEX);
			port = serverCursor.getString(PROJECTION_PORT_INDEX);
			description = serverCursor.getString(PROJECTION_DESCRIPTION_INDEX);
			numplayers = serverCursor.getString(PROJECTION_NUMPLAYERS_INDEX);
			maxplayers = serverCursor.getString(PROJECTION_MAXPLAYERS_INDEX);
			url = serverCursor.getString(PROJECTION_URL_INDEX);
			version = serverCursor.getString(PROJECTION_VERSION_INDEX);

			SpannableString coloredName = ArmaUtils.colourize(name);
			SpannableString coloredDescription = ArmaUtils
					.colourize(description);
			String uncoloredUrl = ArmaUtils.uncolourize(url);

			setContentView(R.layout.activity_server_info);
			((TextView) findViewById(R.id.server_info_server_name)).setText(
					coloredName, TextView.BufferType.SPANNABLE);
			((TextView) findViewById(R.id.server_info_server_ip)).setText(ip);
			((TextView) findViewById(R.id.server_info_server_port))
					.setText(port);
			((TextView) findViewById(R.id.server_info_server_description))
					.setText(coloredDescription, TextView.BufferType.SPANNABLE);
			((TextView) findViewById(R.id.server_info_server_numplayers))
					.setText(numplayers + "/" + maxplayers);
			((TextView) findViewById(R.id.server_info_server_url))
					.setText(uncoloredUrl);
			((TextView) findViewById(R.id.server_info_server_version))
					.setText(version);
			LinearLayout playersContainer = (LinearLayout) findViewById(R.id.server_info_list_of_players);

			String playerGID;
			String playerName;
			LinearLayout.LayoutParams params;
			while (playersCursor.moveToNext()) {
				TextView v = new TextView(this);
				playerName = playersCursor
						.getString(PROJECTION_PLAYER_NAME_INDEX);
				playerGID = playersCursor
						.getString(PROJECTION_PLAYER_GID_INDEX);
				v.setText(ArmaUtils.displayPlayer(playerName, playerGID, true),
						TextView.BufferType.SPANNABLE);
				params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				v.setPadding(0, 6, 0, 6);
				v.setLayoutParams(params);
				v.setMovementMethod(LinkMovementMethod.getInstance());
				playersContainer.addView(v);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CursorIndexOutOfBoundsException e) {
			setContentView(R.layout.activity_server_info_not_found);
		}
		

		View title = findViewById(R.id.server_info_server_name);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View opt = findViewById(R.id.server_info_optional);
				opt.setVisibility(View.VISIBLE);				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_server_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}

}
