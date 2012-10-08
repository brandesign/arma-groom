
	package org.armagetronad.groom;

	import org.armagetronad.groom.content.ArmaContent.Player;
import android.content.Context;
	import android.database.Cursor;
	import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
import android.widget.TextView;

public class PlayersCursorAdapter extends CursorAdapter {


		private LayoutInflater mInflater;
		private int mPlayerId;
		private int mPlayerNameColored;
		private int mPlayerGID;

		public PlayersCursorAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);

			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();

			holder.name.setText(ArmaUtils.colourize(cursor.getString(mPlayerNameColored)),
					TextView.BufferType.SPANNABLE);
			holder.gid.setText(cursor.getString(mPlayerGID));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			
			View v = mInflater.inflate(R.layout.list_item_players, null);
			
	        ViewHolder holder = new ViewHolder();
	        holder.name = (TextView) v.findViewById(R.id.players_list_item_name);
	        holder.gid = (TextView)v.findViewById(R.id.players_list_item_gid);
	        v.setTag(holder);
			
			return v;
		}

		@Override
		public Cursor swapCursor(Cursor newCursor) {
			if (newCursor != null) {
				mPlayerId = newCursor.getColumnIndex(Player._ID);
				mPlayerNameColored = newCursor
						.getColumnIndex(Player.PLAYER_NAME);
				mPlayerGID = newCursor
						.getColumnIndex(Player.PLAYER_GLOBAL_ID);
			}
			return super.swapCursor(newCursor);
		}
		
		static class ViewHolder {
		TextView name;
		TextView gid;
		}


}
