package org.armagetronad.groom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.armagetronad.groom.content.ArmaContent.Player;
import org.armagetronad.groom.content.ArmaProvider;
import org.armagetronad.groom.content.ArmaContent.Server;
import org.armagetronad.groom.content.UpdateDatabaseTask;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	public static final String SERVER_ID = "com.kyo.ServerId";
	public static final int ITEM_SERVERS = 0;
	public static final int ITEM_PLAYERS = 1;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// ImageView bgView = (ImageView) findViewById(R.id.animated_bg);
		// Animation bgSlide = AnimationUtils.loadAnimation(this,
		// R.anim.sliding_background);
		// bgView.startAnimation(bgSlide);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			new UpdateDatabaseTask(this).execute();
			return true;
		case R.id.menu_settings:
			getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void reloadServers(boolean message) {
		try {
			((ServersFragment) mSectionsPagerAdapter.getItem(ITEM_SERVERS))
					.reload(message);
		} catch (IllegalStateException e) {
			Log.e(Constants.TAG, "IllegalStateException. Don't know why...");
			Toast.makeText(this, "Could not update view", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void reloadPlayers(boolean message) {
		try {
			((PlayersFragment) mSectionsPagerAdapter.getItem(ITEM_PLAYERS))
					.reload(message);
		} catch (IllegalStateException e) {
			//TODO fix this
			Log.e(Constants.TAG, "IllegalStateException. Don't know why...");
			Toast.makeText(this, "Could not update view", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private List<Fragment> fragments = new ArrayList<Fragment>(
				Arrays.asList(new Fragment[] { null, null }));

		@Override
		public Fragment getItem(int i) {
			if (fragments.get(i) == null) {
				switch (i) {
				case ITEM_PLAYERS:
					fragments.set(i, new PlayersFragment());
					break;
				case ITEM_SERVERS:
					fragments.set(i, new ServersFragment());
					break;
				}
			}
			return fragments.get(i);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case ITEM_PLAYERS:
				return getString(R.string.title_section_players).toUpperCase();
			case ITEM_SERVERS:
				return getString(R.string.title_section_servers).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * The Players fragment
	 */
	public static class PlayersFragment extends ListFragment implements
	LoaderCallbacks<Cursor> {
		private PlayersCursorAdapter mAdapter;
		private static final String[] PROJECTION = null;
		private static final String SELECTION = null;
		private static final String[] SELECTION_ARGS = null;
		
		public PlayersFragment() {
		}

		public void reload(boolean message) {

			getLoaderManager().restartLoader(0, null, this);
			mAdapter.notifyDataSetChanged();
			if (message) {
				Toast.makeText(getActivity(), "Updated data successfully",
						Toast.LENGTH_SHORT).show();
			}
		}


		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mAdapter = new PlayersCursorAdapter(getActivity(), null, 0);
			setListAdapter(mAdapter);
			getLoaderManager().initLoader(0, null, this);
		}

		// Called when a new Loader needs to be created
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// Now create and return a CursorLoader that will take care of
			// creating a Cursor for the data being displayed.
			return new CursorLoader(getActivity(), ArmaProvider.URI_PLAYERS,
					PROJECTION, SELECTION, SELECTION_ARGS,
					Player.PLAYER_NAME + " ASC");
		}

		// Called when a previously created loader has finished loading
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Swap the new cursor in. (The framework will take care of closing
			// the
			// old cursor once we return.)
			mAdapter.notifyDataSetChanged();
			mAdapter.swapCursor(data);
		}

		// Called when a previously created loader is reset, making the data
		// unavailable
		public void onLoaderReset(Loader<Cursor> loader) {
			// This is called when the last Cursor provided to onLoadFinished()
			// above is about to be closed. We need to make sure we are no
			// longer using it.
			mAdapter.swapCursor(null).close();
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Intent intent = new Intent(getActivity(), ServerInfo.class);
			Cursor c = (Cursor) getListView().getItemAtPosition(position);
			long serverId = c.getLong(c.getColumnIndex(Player.PLAYER_SERVER_ID));
			intent.putExtra(SERVER_ID, serverId);
			startActivity(intent);
		}
	}

	/**
	 * The Servers fragment
	 */
	public static class ServersFragment extends ListFragment implements
			LoaderCallbacks<Cursor> {
		private ServersCursorAdapter mAdapter;
		private static final String[] PROJECTION = null;
		private static final String SELECTION = Server.SERVER_NUMPLAYERS
				+ " > 0";
		private static final String[] SELECTION_ARGS = null;

		public ServersFragment() {
		}

		public void reload(boolean message) {

			getLoaderManager().restartLoader(0, null, this);
			mAdapter.notifyDataSetChanged();
			if (message) {
				Toast.makeText(getActivity(), "Updated data successfully",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mAdapter = new ServersCursorAdapter(getActivity(), null, 0);
			setListAdapter(mAdapter);
			getLoaderManager().initLoader(0, null, this);
		}

		// Called when a new Loader needs to be created
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// Now create and return a CursorLoader that will take care of
			// creating a Cursor for the data being displayed.
			return new CursorLoader(getActivity(), ArmaProvider.URI_SERVERS,
					PROJECTION, SELECTION, SELECTION_ARGS,
					Server.SERVER_NUMPLAYERS + " DESC");
		}

		// Called when a previously created loader has finished loading
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Swap the new cursor in. (The framework will take care of closing
			// the
			// old cursor once we return.)
			mAdapter.notifyDataSetChanged();
			mAdapter.swapCursor(data);
		}

		// Called when a previously created loader is reset, making the data
		// unavailable
		public void onLoaderReset(Loader<Cursor> loader) {
			// This is called when the last Cursor provided to onLoadFinished()
			// above is about to be closed. We need to make sure we are no
			// longer using it.
			mAdapter.swapCursor(null).close();
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Intent intent = new Intent(getActivity(), ServerInfo.class);
			long serverId = getListView().getItemIdAtPosition(position);
			intent.putExtra(SERVER_ID, serverId);
			startActivity(intent);
		}
	}
	
	
	public static class SettingsFragment extends PreferenceFragment {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	    }
	}
	
}
