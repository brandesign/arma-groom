package org.armagetronad.groom.content;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.armagetronad.groom.Constants;
import org.armagetronad.groom.content.ArmaContent.Setting;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.widget.Toast;

public class UpdateDatabaseTask extends AsyncTask<Void, Void, Boolean> {

	private final Context displayContext;

	public UpdateDatabaseTask(Context displayContext) {
		this.displayContext = displayContext;
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(displayContext, "Refreshing...", Toast.LENGTH_LONG)
				.show();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO find a way to cache the date application wide
		ContentProviderClient client = displayContext.getContentResolver()
				.acquireContentProviderClient(ArmaProvider.URI_SETTINGS);
		Cursor cursor;
		Date last = null;
		try {
			String[] projection = new String[] { Setting.SETTING_PARAM,
					Setting.SETTING_VALUE };
			String selection = Setting.SETTING_PARAM + " = ?";
			String[] selectionArgs = new String[] { Setting.KEY_DATE };
			cursor = client.query(ArmaProvider.URI_SETTINGS, projection,
					selection, selectionArgs, null);
			if (cursor.moveToFirst()) {
				String dateStr = cursor.getString(1);
				last = new SimpleDateFormat(ArmaProvider.DATE_FORMAT)
						.parse(dateStr);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			last = null;
		}
		new DownloadXmlTask(displayContext, true, last)
				.execute(Constants.URL_XML_FEEDS);
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Toast.makeText(displayContext, "Downloading Feed from the internet",
				Toast.LENGTH_LONG).show();
		super.onPostExecute(result);
	}

}
