package org.armagetronad.groom;

import org.armagetronad.groom.content.ArmaContent.Server;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServersCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private int mServerId;
	private int mServerName;
	private int mServerNumplayer;
	private int mServerMaxplayers;

	public ServersCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		holder.name.setText(ArmaUtils.colourize(cursor.getString(mServerName)),
				TextView.BufferType.SPANNABLE);
		holder.numplayers.setText(cursor.getString(mServerNumplayer));
		holder.maxplayers.setText(cursor.getString(mServerMaxplayers));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View v = mInflater.inflate(R.layout.list_item_servers, null);
//		won't work to get the list items moving...
//		v.findViewById(R.id.servers_list_item_name).setSelected(true);
		
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) v.findViewById(R.id.servers_list_item_name);
        holder.maxplayers = (TextView)v.findViewById(R.id.servers_list_item_maxplayers);
        holder.numplayers = (TextView)v.findViewById(R.id.servers_list_item_numplayers);
        v.setTag(holder);
		
		return v;
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (newCursor != null) {
			mServerId = newCursor.getColumnIndex(Server._ID);
			mServerName = newCursor
					.getColumnIndex(Server.SERVER_NAME);
			mServerNumplayer = newCursor
					.getColumnIndex(Server.SERVER_NUMPLAYERS);
			mServerMaxplayers = newCursor
					.getColumnIndex(Server.SERVER_MAXPLAYERS);
		}
		return super.swapCursor(newCursor);
	}
	
	static class ViewHolder {
	TextView name;
	TextView numplayers;
	TextView maxplayers;
	}

}
